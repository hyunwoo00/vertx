import React, {useCallback, useState} from 'react';
import {Axios} from "axios";
import {useDispatch} from "react-redux";
import {loginUser} from "../../../_actions/user_action";
import {useNavigate} from "react-router-dom";

const LoginPage = () => {

    const dispatch = useDispatch();

    const navigate = useNavigate();

    const [Email, setEmail] = useState("");
    const [Password, setPassword] = useState("");

    const onEmailHandler = (event) => {
        setEmail(event.currentTarget.value);
    };

    const onPasswordHandler = (event) =>{
        setPassword(event.currentTarget.value);
    };

    const onSubmitHandler = (event) => {
        //페이지 refresh를 막기 위함.
        event.preventDefault();


        let body = {
            email: Email,
            password: Password
        }


        dispatch(loginUser(body))
            .then(response => {
                console.log(response);
                if(response.action.payload.data.loginSuccess){
                    localStorage.setItem("Authorization", response.value.headers["authorization"]);
                    navigate('/');
                } else{
                    alert('로그인 실패');
                }
            });




    };

    return (
        <div style = {{
            display: 'flex', justifyContent: 'center', alignItems:'center',
            width: '100%', height: '100vh'
        }}>
            <form style = {{display: 'flex', flexDirection: 'column'}}
                  onSubmit = {onSubmitHandler}
            >
                <label>Email</label>
                <input type="email" value = {Email} onChange = {onEmailHandler}/>
                <label>Password</label>
                <input type="password" value = {Password} onChange = {onPasswordHandler}/>

                <br/>
                <button>
                    Login
                </button>
            </form>
        </div>
    );
};

export default LoginPage;