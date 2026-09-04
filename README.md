# Scalable and Installable Java App

An educational Java 21 HTTP server repository for learning how concurrency, runtime configuration, packaging, and lifecycle design affect scalability and installability. The project starts with the JDK HTTP server and will later compare embedded Tomcat, embedded Jetty, and Spring Boot using the same HTTP contract. See the [learning roadmap](docs/ROADMAP.md).

## Current state

The repository intentionally begins with build, configuration, concurrency, lifecycle, and container defects. It is learning material, not a production-ready server. Diagnose the baseline before changing `App.java`, `pom.xml`, or `Dockerfile`; commands from the reconstructed challenge may fail until the corresponding milestone is completed.

## Prerequisites

- Java 21
- Apache Maven
- Docker, for the container milestones

The repository includes an optional `.sdkmanrc` pin for SDKMAN users. After cloning, activate it with `sdk env`; otherwise select Java 21 using your preferred JDK manager. Verify the active toolchain before starting:

```shell
java --version
mvn --version
```

Both commands should report Java 21. Configure your IDE and its Maven runner to use the same JDK.

## Reconstructed challenge statement

این پروژه یک اپلیکیشن ساده با Java 21 و Maven است. هدف تمرین، شناسایی و رفع مشکلات مقیاس‌پذیری و نصب‌پذیری پروژه است؛ بدون آن‌که رفتار اصلی endpoint تغییر کند.

## چالش‌ها

### 1. مقیاس‌پذیری (Scalability)

اپلیکیشن فعلی از executor پیش‌فرض استفاده می‌کند و توانایی پاسخ‌گویی هم‌زمان به چندین درخواست را ندارد. در حال حاضر سرور به‌صورت تک‌ریسمانی (`single-threaded`) اجرا می‌شود. ساختار پروژه را طوری تغییر دهید که سرور بتواند به تعداد زیادی درخواست هم‌زمان پاسخ دهد.

### 2. نصب‌پذیری (Installability)

در اجرای پروژه و تغییر تنظیمات محیطی مشکلاتی وجود دارد. فرایند build، فایل `Dockerfile` و پیکربندی پروژه را طوری اصلاح کنید که پروژه روی محیط‌های مختلف به‌سادگی و به‌صورت خودکار، بدون نیاز به تنظیمات دستی، build و اجرا شود.

## راهنمای اجرا

1. باید Java 21 و Maven روی سیستم نصب باشند.
2. برای اجرای پروژه به‌صورت محلی:

   ```shell
   mvn clean compile exec:java
   ```

3. برای اجرای پروژه با Docker:

   ```shell
   docker build -t scalable-java-app .
   docker run -p 8080:8080 scalable-java-app
   ```

پس از اجرا، endpoint زیر باید در دسترس باشد:

```text
GET http://localhost:8080/hello
```

پاسخ مورد انتظار:

```text
Hello, World!
```

## نکات

- فرایند build و deploy باید پایدار باشد و روی محیط‌های مختلف به‌شکل یکسان کار کند.
- امکان تغییر پورت و سایر تنظیمات از طریق متغیرهای محیطی فراهم شود و مقدار پیش‌فرض مناسبی وجود داشته باشد.
- مدیریت thread pool و منابع آن باید به‌درستی انجام شود.

## Hint

- برای مقیاس‌پذیری، نحوه استفاده از `Executor` و مدیریت چرخه عمر منابع را بررسی کنید.
- قابلیت تنظیم پیکربندی از طریق متغیرهای محیطی و اجرای مستقل با Docker را در نظر بگیرید.
- اطمینان حاصل کنید dependencyها و ابزارهای لازم برای Java 21 هنگام build روی سیستم‌عامل‌های مختلف در دسترس هستند.

> این صورت‌مسئله از روی تصاویر آزمون بازسازی شده است. عبارت‌بندی برای خوانایی یکدست شده، اما الزامات و ایرادهای قابل مشاهده عمداً حفظ شده‌اند.
