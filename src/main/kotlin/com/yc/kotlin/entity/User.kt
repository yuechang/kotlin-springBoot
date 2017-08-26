package com.yc.kotlin.entity

import javax.validation.constraints.NotNull
import java.io.Serializable
import javax.persistence.*
/**
 * Created by Administrator on 2017/8/24.
 */
@Entity @Table(name = "user")
data class User(@Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long? = 0,
                @Column(nullable = false) var name: String? = null,
                @Column(nullable = false) var email: String? = null) : Serializable {
    protected constructor() : this(id = null, name = null, email = null) { }
}
