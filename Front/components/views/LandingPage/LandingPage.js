import React,{useEffect} from 'react';
import axios from "axios";
import {useDispatch, useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";
import {deleteUser, logoutUser} from "../../../_actions/user_action";

const LandingPage = () => {

    const navigate = useNavigate();
    const dispatch = useDispatch();

    useEffect(() => {
        axios.get('/api/hello')
            .then(response => console.log(response.data))
    }, []);

    //store에 있는 값을 가져옴.
    const isLogin = useSelector(stateObj => {
        //console.log(stateObj);
        return stateObj.user.isAuth;
    });

    const email = useSelector(stateObj => stateObj.user.email);

    const goToLogin = () => {
        navigate('/login');
    }

    const logout = () => {

        dispatch(logoutUser())
            .then(response => {
                console.log(response);
            });

    };

    const onClickHandler = () => {
        if(isLogin){
            logout();
        }
        else{
            goToLogin();
        }
    }

    const deleteHandler = () => {
        dispatch(deleteUser(email))
            .then(response => {
                console.log(response);
                if(response.action.payload.delete){
                    alert('회원탈퇴 성공');
                } else{
                    alert('회원탈퇴 실패');
                }
            });
    };


    return (
        <div style = {{
            display: 'flex', justifyContent: 'center', alignItems:'center',
            width: '100%', height: '100vh'
        }}>
            <h2>시작 페이지</h2>

            <button onClick = {onClickHandler} >
                {isLogin ? '로그아웃' : '로그인'}
            </button>

            <button onClick = {deleteHandler} hidden = {!isLogin}>
                회원탈퇴
            </button>


        </div>
    );
};

export default LandingPage;