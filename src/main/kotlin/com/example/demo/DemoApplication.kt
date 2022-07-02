package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.BodyExtractors.toMono
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RequestPredicates.POST
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono
import java.util.UUID

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}

data class User(
    @Id
    val id: String,
    val username: String
)

@Repository
interface UserRepository : ReactiveMongoRepository<User, UUID>

@Configuration
class Config(
) {

    @Bean
    fun saveData(users: UserRepository) = route(POST("saveData")) { req ->
        val user = req.bodyToMono(User::class.java)
        ok().body(user.flatMap(users::save).doOnSuccess { println(it) }, User::class.java)
    }

    @Bean
    fun getData(users: UserRepository) = route(GET("/getData")) { req ->
        ok().body(users.findAll(), User::class.java)
    }


}

