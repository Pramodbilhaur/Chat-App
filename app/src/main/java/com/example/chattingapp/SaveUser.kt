package com.example.chattingapp

class SaveUser {

    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var mobile: String? = null

    constructor(){}

    constructor(name: String?, email: String?, uid: String?, mobile: String?){
        this.name = name
        this.email = email
        this.uid = uid
        this.mobile = mobile
    }
}