import {ActionType} from "redux-promise-middleware";

export const LOGIN_USER = "login_user";
export const LOGIN_USER_PENDING = `login_user_${ActionType.Pending}`;
export const LOGIN_USER_FULFILLED = `login_user_${ActionType.Fulfilled}`;
export const LOGIN_USER_REJECTED = `login_user_${ActionType.Rejected}`;

export const LOGOUT_USER = "logout_user";
export const LOGOUT_USER_PENDING = `logout_user_${ActionType.Pending}`;
export const LOGOUT_USER_FULFILLED = `logout_user_${ActionType.Fulfilled}`;
export const LOGOUT_USER_REJECTED = `logout_user_${ActionType.Rejected}`;

export const REGISTER_USER = "register_user";
export const REGISTER_USER_PENDING = `register_user_${ActionType.Pending}`;
export const REGISTER_USER_FULFILLED = `register_user_${ActionType.Fulfilled}`;
export const REGISTER_USER_REJECTED = `register_user_${ActionType.Rejected}`;

export const DELETE_USER = "delete_user";
export const DELETE_USER_PENDING = `delete_user_${ActionType.Pending}`;
export const DELETE_USER_FULFILLED = `delete_user_${ActionType.Fulfilled}`;
export const DELETE_USER_REJECTED = `delete_user_${ActionType.Rejected}`;

export const AUTH_USER = "auth_user";
export const AUTH_USER_PENDING = `auth_user_${ActionType.Pending}`;
export const AUTH_USER_FULFILLED = `auth_user_${ActionType.Fulfilled}`;
export const AUTH_USER_REJECTED = `auth_user_${ActionType.Rejected}`;