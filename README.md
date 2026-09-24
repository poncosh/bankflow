# BankFlow Chapter 08

BankFlow adalah contoh aplikasi microservices untuk pembuatan rekening, validasi
transfer, publikasi event transfer, dan simulasi notifikasi. Akses API publik masuk
melalui API Gateway dan lokasi service diperoleh dari Eureka Service Registry.

## Arsitektur

| Service | Port default | Tanggung jawab |
| --- | ---: | --- |
| Service Registry | `8761` | Registrasi dan discovery service menggunakan Eureka |
| API Gateway | `8080` | Routing berbasis discovery, correlation ID, dan rate limiting |
| Account Service | `8081` | Membuat serta membaca rekening dan menyediakan lookup internal |
| Transfer Service | `8082` | Memvalidasi rekening, membuat transfer, dan menerbitkan event Kafka |
| Notification Service | `8083` | Mengonsumsi event transfer dengan retry dan penanganan error |
| Kafka | `9092` | Broker event `bankflow.transfer.completed.v1` |

Alur utama:

```text
Client
  -> API Gateway
      -> Account Service
      -> Transfer Service -> Account Service
                          -> Kafka -> Notification Service
```

## Resilience

Transfer Service melindungi lookup Account Service dengan instance
`accountService`:

- Circuit breaker membuka setelah minimum 5 pemanggilan dengan failure rate 50%.
- State `OPEN` berpindah otomatis ke `HALF_OPEN` setelah 10 detik.
- Tiga pemanggilan diizinkan untuk menguji pemulihan pada `HALF_OPEN`.
- Retry maksimal 3 kali hanya untuk timeout atau dependency unavailable.
- Account not found dan invalid response tidak di-retry serta tidak dihitung sebagai
  kegagalan circuit breaker.
- Fallback selalu mengembalikan dependency error dan tidak membuat keberhasilan
  transaksi palsu.

API Gateway menggunakan rate limiter `transferApi`: maksimal 5 request ke
`/api/transfers` dalam 10 detik. Request berikutnya mendapatkan HTTP `429` dengan
kode `RATE_LIMIT_EXCEEDED` dan header `Retry-After`.

## Prasyarat

- Java 21
- Maven 3.9+
- Docker dengan Docker Compose

Versi utama proyek adalah Spring Boot `4.0.8`, Spring Cloud `2025.1.3`, dan Kafka
`4.1.2`.

## Menjalankan aplikasi

Jalankan Kafka dari root project:

```powershell
docker compose up -d
```

Kemudian jalankan setiap perintah berikut pada terminal terpisah dan sesuai urutan:

```powershell
mvn -f service-registry/pom.xml spring-boot:run
mvn -f account-service/pom.xml spring-boot:run
mvn -f transfer-service/pom.xml spring-boot:run
mvn -f notification-service/pom.xml spring-boot:run
mvn -f api-gateway/pom.xml spring-boot:run
```

Dashboard Eureka tersedia di `http://localhost:8761`. Health API Gateway dapat
diperiksa melalui `http://localhost:8080/actuator/health`.

Konfigurasi yang dapat dioverride melalui environment variable:

| Variable | Default | Kegunaan |
| --- | --- | --- |
| `EUREKA_SERVER_URL` | `http://localhost:8761/eureka/` | Lokasi Eureka server |
| `EUREKA_INSTANCE_HOSTNAME` | `service-registry` | Identitas node Eureka Server |
| `EUREKA_REPLICA_HOSTNAME` | `localhost` | Domain yang ditampilkan pada DS Replicas |
| `ACCOUNT_CONNECT_TIMEOUT` | `2s` | Timeout koneksi ke Account Service |
| `ACCOUNT_READ_TIMEOUT` | `3s` | Timeout membaca respons Account Service |
| `ACCOUNT_CLIENT_DELAY_MS` | `0` | Delay sintetis khusus pengujian resilience |

## API melalui Gateway

Base URL: `http://localhost:8080`

| Method | Endpoint | Kegunaan |
| --- | --- | --- |
| `POST` | `/api/accounts` | Membuat rekening |
| `GET` | `/api/accounts` | Melihat semua rekening |
| `GET` | `/api/accounts/{id}` | Melihat detail rekening |
| `GET` | `/api/accounts/{id}/balance` | Melihat saldo rekening |
| `GET` | `/api/accounts/{id}/status` | Melihat status rekening |
| `POST` | `/api/transfers` | Membuat dan memvalidasi transfer |
| `GET` | `/api/transfers` | Melihat semua transfer |
| `GET` | `/api/transfers/{id}` | Melihat detail transfer |

Collection siap impor tersedia di
[`postman/BankFlow_Chapter_08.postman_collection.json`](postman/BankFlow_Chapter_08.postman_collection.json).

## Menjalankan test

```powershell
mvn -f account-service/pom.xml test
mvn -f api-gateway/pom.xml test
mvn -f notification-service/pom.xml test
mvn -f service-registry/pom.xml test
mvn -f transfer-service/pom.xml test
```

Evidence pengujian healthy route, timeout, circuit breaker, retry, recovery, rate
limiter, health, dan hasil Maven tersedia di [`evidence/chapter-08`](evidence/chapter-08).
Checklist akhir tersedia di
[`evidence/chapter-08/acceptance-checklist.md`](evidence/chapter-08/acceptance-checklist.md).
