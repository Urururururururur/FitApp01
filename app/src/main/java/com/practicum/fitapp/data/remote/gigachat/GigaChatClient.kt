package com.practicum.fitapp.data.remote.gigachat

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

object GigaChatClient {
    private const val BASE_URL = "https://api.giga.chat/"

    fun createApi(context: Context): GigaChatApi {
        // 1. Загружаем сертификат из res/raw (как обсуждали)
        val certStream = context.resources.openRawResource(com.practicum.fitapp.R.raw.russian_trusted_root_ca)
        val cf = CertificateFactory.getInstance("X.509")
        val caCert = cf.generateCertificate(certStream)

        // 2. Создаем KeyStore и добавляем сертификат
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            load(null, null)
            setCertificateEntry("gigachat_ca", caCert)
        }

        // 3. Настраиваем TrustManager
        val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
            init(keyStore)
        }

        // 4. Создаем SSLContext
        val sslContext = SSLContext.getInstance("TLS").apply {
            init(null, tmf.trustManagers, null)
        }

        // 5. Настраиваем логирование (чтобы видеть запросы в Logcat)
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC // Теперь будем видеть только URL и код ответа
        }

        // 6. Собираем OkHttpClient
        val client = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, tmf.trustManagers[0] as javax.net.ssl.X509TrustManager)
            .addInterceptor(logging)
            .connectTimeout(60, TimeUnit.SECONDS) // Даем время нейросети подумать
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        // 7. Создаем Retrofit
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GigaChatApi::class.java)
    }
}