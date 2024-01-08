import {combineReducers} from "redux";
import user from './user_reducer';


//여러 개의 reducer들을 하나의 reducer로 만들어줌.
const rootReducer = combineReducers({
    user
})

export default rootReducer;