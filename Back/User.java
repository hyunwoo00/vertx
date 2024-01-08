package org.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.authorization.Authorization;

import java.util.HashSet;
import java.util.Set;

public class User{
    private String name;
    private String email;
    private String password;
    private Set<Authorization> authorizationSet;


    public User(String name, String email, String passWord,Set<Authorization> authorizationSet) {
        this.name = name;
        this.email = email;
        this.password = passWord;
        this.authorizationSet = authorizationSet;
    }

    public Set<Authorization> getAuthorizationSet() {
        return authorizationSet;
    }

    public void setAuthorizationSet(Set<Authorization> authorizationSet) {
        this.authorizationSet = authorizationSet;
    }

    public User(JsonObject jsonObject){
        this.name = jsonObject.getString("name");
        this.email = jsonObject.getString("email");
        this.password = jsonObject.getString("password");

        JsonArray auths = jsonObject.getJsonArray("auths");
        if(auths != null) {
            this.authorizationSet = new HashSet<>(auths.getList());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassWord() {
        return password;
    }

    public void setPassWord(String passWord) {
        this.password = passWord;
    }

    public JsonObject userInfo(){
        JsonObject obj = new JsonObject();

        obj.put("name", name);
        obj.put("email", email);
        obj.put("password", password);

        //Set은 JsonObject에 넣어줄 수 없어서 JsonArray로 변환 후 넣어줌.
        JsonArray auths = new JsonArray();
        auths.add(authorizationSet);

        obj.put("auths", auths);

        return obj;
    }




}
