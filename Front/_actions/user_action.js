import axios from "axios";

import {
    LOGIN_USER,
    LOGOUT_USER,
    REGISTER_USER,
    AUTH_USER, DELETE_USER
} from "./types"


//클라이언트에서 서버로 요청을 보낼 때 헤더에 토큰을 포함시킴.
axios.interceptors.request.use((config) => {
   config.headers["authorization"] = localStorage.getItem("Authorization");

   return config;
});


export function loginUser(dataTosubmit){

    const request = axios.post('/api/user/login', dataTosubmit)
        //.then(response => response.data);

    return {
        type: LOGIN_USER,
        payload: request
    };
}
export function logoutUser(){

    const request = axios.get('/api/user/logout')
        .then(response => response.data);

    return {
        type: LOGOUT_USER,
        payload: request
    };
}

export function registerUser(dataTosubmit){

    const request = axios.post('/api/user/register', dataTosubmit)
        .then(response => response.data);

    return {
        type: REGISTER_USER,
        payload: request
    };
}

export function deleteUser(userEmail){

    console.log(userEmail);
    const request = axios.delete(`/api/user/delete/${userEmail}`)
        .then(response => response.data);

    return {
        type: DELETE_USER,
        payload: request
    };
}

export function auth(){


    const request = axios.get('/api/user/auth')
        .then(response => response.data);

    return {
        type: AUTH_USER,
        payload: request
    };
}


