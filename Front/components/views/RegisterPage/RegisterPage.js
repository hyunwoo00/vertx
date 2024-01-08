import React, {useState} from 'react';
import {useDispatch} from "react-redux";
import {useNavigate} from "react-router-dom";
import {registerUser} from "../../../_actions/user_action";

const RegisterPage = () => {
    const dispatch = useDispatch();

    const navigate = useNavigate();

    const [Email, setEmail] = useState("");
    const [Name, setName] = useState("");
    const [Password, setPassword] = useState("");
    const [ConfirmPassword, setConfirmPassword] = useState("");
    const onEmailHandler = (event) => {
        setEmail(event.currentTarget.value);
    };
    const onNameHandler = (event) => {
        setName(event.currentTarget.value);
    };

    const onPasswordHandler = (event) =>{
        setPassword(event.currentTarget.value);
    };
    const onConfirmPasswordHandler = (event) =>{
        setConfirmPassword(event.currentTarget.value);
    };

    const onSubmitHandler = (event) => {
        //페이지 refresh를 막기 위함.
        event.preventDefault();

        if(Password !== ConfirmPassword){
            return alert('비밀번호가 일치하지 않습니다.');
        }


        let body = {
            email: Email,
            password: Password,
            name: Name
        }

        dispatch(registerUser(body))
            .then(response => {
                if (response.action.payload.register) {
                    navigate('/');
                } else {
                    alert('이미 존재하는 이메일입니다.');
                }
            });
    }

    return (
        <div style={{
            display: 'flex', justifyContent: 'center', alignItems: 'center',
            width: '100%', height: '100vh'
        }}>
            <form style={{display: 'flex', flexDirection: 'column'}}
                  onSubmit={onSubmitHandler}
            >
                <label>Email</label>
                <input type="email" value={Email} onChange={onEmailHandler}/>
                <label>Name</label>
                <input type= "text" value={Name} onChange={onNameHandler}/>
                <label>Password</label>
                <input type="password" value={Password} onChange={onPasswordHandler}/>
                <label>Confirm Password</label>
                <input type="password" value={ConfirmPassword} onChange={onConfirmPasswordHandler}/>

                <br/>
                <button>
                    회원 가입
                </button>
            </form>
        </div>
    );
};

export default RegisterPage;