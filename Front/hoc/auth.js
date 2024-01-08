import React, {useEffect} from 'react';
import axios from "axios";
import {useDispatch} from "react-redux";
import {auth} from "../_actions/user_action";
import {useNavigate} from "react-router-dom";

export default function(SpecificComponent, option, adminRoute = null) {
    //option
    //null => 아무나 출입 가능
    //true => 로그인한 유저만 출입 가능
    //false => 로그인한 유저는 출입 불가능
    const dispatch = useDispatch();
    const navigate = useNavigate()

    useEffect(() => {
        dispatch(auth()).then(response => {
            console.log(response);

            //로그인하지 않은 상태
            if(!response.action.payload.isAuth){
                if(option){
                    navigate('/login');
                }
            }
            //로그인한 상태
            else{
                if(!option){
                    navigate('/');
                }
            }

        });

    }, []);

    return (
        <SpecificComponent />
    );


}