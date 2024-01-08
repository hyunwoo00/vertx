import {
    LOGIN_USER_PENDING,
    LOGIN_USER_FULFILLED,
    LOGIN_USER_REJECTED,
    LOGOUT_USER_PENDING,
    LOGOUT_USER_FULFILLED,
    LOGOUT_USER_REJECTED,
    REGISTER_USER_REJECTED,
    REGISTER_USER_PENDING,
    REGISTER_USER_FULFILLED,
    DELETE_USER_REJECTED,
    DELETE_USER_PENDING,
    DELETE_USER_FULFILLED,
    AUTH_USER_REJECTED,
    AUTH_USER_PENDING,
    AUTH_USER_FULFILLED, LOGIN_USER, REGISTER_USER, AUTH_USER
} from "../_actions/types";

const initialState = {
    loginSuccess: false,
    register: false,
    isAuth: false,
    delete: false,
    email: "",
};

export default function(state = initialState, action){

    switch (action.type){
        //login
        case LOGIN_USER_PENDING:
            return state;

        case LOGIN_USER_FULFILLED:
            return {
                ...state,
                isFulfilled: true,
                loginSuccess: action.payload.data.loginSuccess,
                isAuth: action.payload.data.loginSuccess,
                email: action.payload.data.email
            }

        case LOGIN_USER_REJECTED:
            return {
                ...state,
                isRejected: true,
                error: action.payload
            };
        //logout
        case LOGOUT_USER_PENDING:
            return state;

        case LOGOUT_USER_FULFILLED:
            return {
                ...state,
                isFulfilled: true,
                isAuth: action.payload.isAuth
            }

        case LOGOUT_USER_REJECTED:
            return {
                ...state,
                isRejected: true,
                error: action.payload
            };
        //Register
        case REGISTER_USER_PENDING:
            return state;

        case REGISTER_USER_FULFILLED:
            return {
                ...state,
                isFulfilled: true,
                register: action.payload.register
            }

        case REGISTER_USER_REJECTED:
            return {
                ...state,
                isRejected: true,
                error: action.payload
            };

        //Delete
        case DELETE_USER_PENDING:
            return state;

        case DELETE_USER_FULFILLED:
            return {
                ...state,
                isFulfilled: true,
                delete: action.payload.delete,
                isAuth: !(action.payload.delete)
            }

        case DELETE_USER_REJECTED:
            return {
                ...state,
                isRejected: true,
                error: action.payload
            };
        //auth
        case AUTH_USER_PENDING:
            return state;

        case AUTH_USER_FULFILLED:
            return {
                ...state,
                isFulfilled: true,
                isAuth: action.payload.isAuth,
            }

        case AUTH_USER_REJECTED:
            return {
                ...state,
                isRejected: true,
                error: action.payload
            };



        default: return state;
    }

}