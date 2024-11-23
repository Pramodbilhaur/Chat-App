package com.example.chattingapp

import com.google.auth.oauth2.GoogleCredentials
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

object Accesstoken {
    
    private val firebaseMessagingScope: String = "https://www.googleapis.com/auth/firebase.messaging"

    fun getAccessToken(): String? {
        try{
            val jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"chat-app-9c597\",\n" +
                    "  \"private_key_id\": \"cd23ec267d98c06e6b972caf1f3f20c4af424ce3\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCldz35k5aL15L8\\nAp+NvpUi929iRj5qIXJ62icVi9boOIuqhaPVEn3BOnVX4eabjw3r9ejkJVuebtcY\\ngkImX2sOkbxpFfqZRRSGr1GyvLoJF51qf9eJnKaMCFOXZxeo8LMQiHdF+JTEwwe7\\nkVqiowTiSV9GhCH90XPd+T8ibgDM50YNs5c3U4hO1xWfqBb87KaHR83gQsCm4CZQ\\nM2vzXgKcDNRw1IEDHMzqY//Mv/NobV9LdL2isuMiPqPB8yPOA0hbqdUnm+W2ZYHv\\n45sWCJaOFm6VkE8NXgbvxzxp+O0kkUDI+3ziiOpCYLiev8niVD6JaIpt24Rt1FXV\\nU4NWMIQhAgMBAAECggEAEAMYBjzYBrRXzvdaBVd6CyYyqyntBEw0TdsqiRU9OL+w\\nFFJWZlohvT8w984uiMMxL7JeVSWgVhaXzhFc4WW+cGx99RVhaM+ciuQQXeGRhyu3\\n2gSBERoNq+NqY25ghcK1MVZjZGwHyXquFuKcCL0JzfQXujUzLOLF+acTCvvmK+3V\\nlJwrTcqfoNiyEFDR3dFFlZfPHfyOTPGb2s0pZmFm0RK26eYHc/PC+QD2akRQ5U7v\\n7F/rPGGEyE/ECEwiJBQjKK+kwgjXIV8+2WdGHh91E05eM8NVs6eavRdLRfcwabwK\\nzJ5LlIxbSkUl9gZxhEdMu2MFfQhl2T5SHEDtJa5keQKBgQC/vMA22Yl9PrFe2iJ0\\n8anIyHNZc0tzbNymYQG1hzFkyQUlnXvPkJxdiecXw1+u0nMWfgIWXLBn6JgBMa9c\\n9QklK/oyzmh5aNnNuJpJa/v5CmpH1ByYTPws79IEEYaFmTSUFpARRbiVa38g8R3s\\nFoOFjng5ldnlaqPcZVEbw1ufcwKBgQDc7F6Cwnbpvge5zG3j+duidBjOSce9OLnC\\ncM7jkeyg9CYeiI0IjrVwO+tKPMXICDg8jyEhU911lLGubbR13XyhatR4YYhOQx29\\nEfvBVyexV/A6XkBj4YKc26CZQNrGco8cPcyIUae6295vQ9uG+wKnmGAYAasfOpB9\\niBs2oCvBGwKBgGwVqwVAfBDuVm5x6RqjYGKfHOc/5ziaFYh1BtGd9GosJ4qczD6l\\n9n+ETZFHwZh7agUz41lXxtnMdGO37nMobo9VyyT41kwany0o81CQXup0xJTZLYVt\\nYHq0dl90wbBuPQV98pjKBNO2VWPsiEGeL2YKeht8Lej1xIlpU9+isPOhAoGBAMef\\nRV79/JkEvtJiKEHBOXa3VNrBzBvsr91ENY1oA9sEAZXBaT58RPIfEmJIO6ad1sLR\\nyevlbcybCVpFgrSl58GrWEVWS+X7u1lmcIepCxKyFT2IgMI/uNenlJwmAAkk5q9s\\nH+5lahvsJDeBp1F2alWp19qZV1imgH0NiRl47VBVAoGABk7o2eqO5lvUfUkEHsg3\\nz76THuctGw9jxcWObXgwf8Qrc8U5svCjQGz+qnI46GhJ31SG+u9XIPznXf5lLZna\\nhCRE1v3YU28pBjbS9hL4FgnkdE/0bgkWgPyLiRTpOCQiAW/wvgmMd2kSM+or6VGf\\nurhMuVXAu5DDPxFF+aO6UI8=\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-1bj78@chat-app-9c597.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"116327938367889662554\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-1bj78%40chat-app-9c597.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}"

            val stream = ByteArrayInputStream(jsonString.toByteArray(StandardCharsets.UTF_8))

            val googleCredential = GoogleCredentials.fromStream(stream)
                .createScoped(arrayListOf(firebaseMessagingScope))

            googleCredential.refresh()

            return googleCredential.accessToken.tokenValue
        } catch (e: IOException){
            return null
        }
    }
}