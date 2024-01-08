package org.vertx;

import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.authorization.Authorization;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.auth.authorization.PermissionBasedAuthorization;
import io.vertx.ext.auth.authorization.RoleBasedAuthorization;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptionsConverter;
import io.vertx.ext.auth.jwt.authorization.MicroProfileAuthorization;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import org.bson.Document;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.HashSet;
import java.util.Set;

public class MainVerticle extends AbstractVerticle {
    // 비밀 암호
    // 배포 시 따로 파일을 만들어 변수를 저장해주고 .gitignore에 추가해야 함.
    private static final String SECRET = Secret.getSecret();

    private JWTAuth provider;

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    private MongoCollection<Document> tokenBlackListCollection;

    private void initProvider(){

        provider = JWTAuth.create(vertx, new JWTAuthOptions()
                .addPubSecKey(new PubSecKeyOptions()
                        //SECRET을 대칭 키로 사용하는 대칭 키 알고리즘
                        .setAlgorithm("HS256")
                        .setBuffer(SECRET)));
    }
    //토큰 생성 함수
    private String generateToken(JsonObject obj){
        //jwt의 payload의 내용.
        // 유출 가능성이 있기 때문에 비밀번호 같은 중요한 내용은 담으면 안 됨.
        String email = obj.getString("email");
        String name = collection.find(new Document("email", email)).iterator().next().getString("name");

        JsonObject payload = new JsonObject()
                .put("email", email)
                .put("name", name);

        //유효 기간 설정
        JWTOptions jwtOptions = new JWTOptions().setExpiresInMinutes(60);

        String token = provider.generateToken(payload, jwtOptions);

        return token;
    }



    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        super.start(startPromise);

        //토큰 provider 생성
        initProvider();


        client = MongoClients.create();
        database = client.getDatabase("web");
        collection = database.getCollection("user");
        tokenBlackListCollection = database.getCollection("tokenBlackList");


        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());



        //회원가입
        router.post("/api/user/register").handler(routingContext -> {

            JsonObject obj = routingContext.body().asJsonObject();

            User user = new User(obj);

            String password = user.getPassWord();
            String email = user.getEmail();

            MongoCursor<Document> userInfos =  collection.find(new Document("email", email)).iterator();

            //이메일이 존재하는 경우
            if(userInfos.hasNext()){
                routingContext.response()
                     //   .setStatusCode(400)
                        .end(new JsonObject().put("register", false).toString());
                return;
            }

            Set<Authorization> auths = new HashSet<Authorization>();

            //비밀번호 암호화
            String encrypted = BCrypt.hashpw(password, BCrypt.gensalt());
            //암호화된 비밀번호를 데이터베이스에 넣음.
            user.setPassWord(encrypted);

            //유저라는 권한 부여.
            auths.add(RoleBasedAuthorization.create("user"));
            user.setAuthorizationSet(auths);



            collection.insertOne(Document.parse(user.userInfo().toString()));


            routingContext.response()
                    .setStatusCode(200)
                    .end(new JsonObject().put("register", true).toString());
        });

        //로그인
        router.post("/api/user/login").handler(routingContext -> {
            JsonObject obj = routingContext.body().asJsonObject();

            String email = obj.getString("email");
            String password = obj.getString("password");
            MongoCursor<Document> userInfos =  collection.find(new Document("email", email)).iterator();

            //데이터베이스에 이메일이 존재하지 않는 경우
            if(!userInfos.hasNext()){
                routingContext.response()
                     //   .setStatusCode(400)
                        .end(new JsonObject().put("loginSuccess", false).toString());
                return;
            }

            Document userInfo = userInfos.next();

            //데이터베이스에 저장된 유저의 비밀번호를 get.
            String userPassWord = userInfo.getString("password");

            // 비밀번호 일치
            if(BCrypt.checkpw(password, userPassWord)){

                //토큰 생성
                String token = generateToken(obj);

                routingContext.response()
                        .setStatusCode(200)
                        .putHeader("Authorization", "Bearer " + token)
                        .end(new JsonObject().put("loginSuccess", true).put("email", email).toString());
            }

            //비밀번호 불일치
            else{
                routingContext.response()
                        .setStatusCode(401)
                        .end(new JsonObject().put("loginSuccess", false).toString());
                return;
            }
        });

        //로그아웃
        router.get("/api/user/logout").handler(routingContext -> {
            //클라이언트로부터 토큰을 가져옴.
            String token = routingContext.request().getHeader("authorization");
            //블랙리스트에 추가
            tokenBlackListCollection.insertOne(new Document().append("token", token));

            routingContext.response()
                    .setStatusCode(200)
                    .end(new JsonObject().put("isAuth", false).toString());

        });

        //회원탈퇴
        router.delete("/api/user/delete/:userEmail").handler(routingContext -> {

            //클라이언트로부터 토큰을 가져옴.
            String token = routingContext.request().getHeader("authorization");
            //블랙리스트에 추가
            tokenBlackListCollection.insertOne(new Document().append("token", token));

            String email = routingContext.pathParam("userEmail");

            DeleteResult dr = collection.deleteOne(new Document("email", email));

            //삭제한 데이터가 없을 경우
            if(dr.getDeletedCount() == 0){
                routingContext.response()
                        .end(new JsonObject().put("delete", false).toString());
                return;
            }
            //삭제 성공
            routingContext.response()
                    .setStatusCode(200)
                    .end(new JsonObject().put("delete", true).toString());

        });

        //Authentication
        //JWT인증이 성공하면 다음 handler로 넘김.
        router.get("/api/user/auth").handler(JWTAuthHandler.create(provider));

        router.get("/api/user/auth").handler(routingContext -> {

            //클라이언트로부터 토큰을 가져옴.
            String token = routingContext.request().getHeader("authorization");

            MongoCursor<Document> blacklist = tokenBlackListCollection.find(new Document("token", token)).iterator();

            // 블랙리스트 내에 클라이언트가 보낸 토큰이 존재하는 경우.
            if(blacklist.hasNext()){
                routingContext.response()
                        .end(new JsonObject().put("isAuth", false).toString());
                return;
            }

            routingContext.response()
                    .setStatusCode(200)
                    //routingContext.user().principal()은 jwt의 payload내용을 담고있음.
                    .end(routingContext.user().principal().put("isAuth", true).toString());
        });

        //인증에 실패했을 때 handling
        router.get("/api/user/auth").failureHandler(failureRoutingContext -> {

            failureRoutingContext.response()
                    .setStatusCode(200)
                    .end(new JsonObject().put("isAuth", false).toString());

        });


        router.get("/api/hello").handler(routingContext -> {
            routingContext.response().send("안녕하세요");
        });

        server.requestHandler(router).listen(5000);
    }


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new MainVerticle());
    }
}
