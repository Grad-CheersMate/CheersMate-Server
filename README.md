# 🍻 CheersMate

> **AI를 활용한 주류 및 안주 추천 서비스**  
> **사용자의 기분, 날씨, 동행자와 같은 상황 정보를 기반으로 최적의 주류와 안주 조합을 AI가 추천해 드립니다.**  
> **약 6,000개 이상의 다양한 주류 데이터베이스를 보유하고 있어 풍부한 선택지를 제공합니다.**

---

## 📋 목차
- [프로젝트 개요](#-프로젝트-개요)
- [주요 기능](#-주요-기능)
- [기술 스택](#-기술-스택)
- [시스템 아키텍처](#-시스템-아키텍처)
- [프로젝트 구조](#-프로젝트-구조)
- [API 문서](#-api-문서)
- [실행 방법](#-실행-방법)

---

## 📷 **프로젝트 개요**
### 🔹 CheersMate란?
CheersMate는 사용자의 상황과 선호도에 맞는 최적의 주류와 안주 조합을 AI 기반으로 추천해주는 서비스입니다. 현재 날씨, 기분, 함께하는 사람 등 다양한 상황 정보를 입력하면 AI가 분석하여 최적의 음료와 안주를 추천해줍니다.

### 🔹 개발 배경 및 목적
- 소비자들이 다양한 주류를 쉽게 탐색하고 경험할 수 있는 플랫폼 개발
- 개인의 상황과 선호도에 맞춘 맞춤형 추천 시스템 구현
- 날씨 API 연동을 통한 실시간 상황 기반 추천 서비스 제공
- 사용자 피드백을 통한 추천 시스템 구현

---

## ⚙️ **주요 기능**
### 📌 **1. AI 기반 주류 및 안주 추천 시스템**
- 사용자 상황 정보(날씨, 기분, 동행자 등) 기반 맞춤형 추천
- Flask 기반 AI 모델과 Spring Boot 백엔드 연동
- 약 6,000개 이상의 주류 데이터베이스 활용
- 사용자 피드백을 통한 추천 알고리즘 개선

### 📌 **2. 실시간 날씨 정보 연동 시스템**
- 외부 날씨 API 연동을 통한 실시간 날씨 정보 수집
- 현재 날씨에 적합한 주류 및 안주 추천
- 계절 및 기후 특성을 고려한 맞춤형 추천

### 📌 **3. 커뮤니티 시스템**
- 사용자 간 주류 및 안주 정보 공유
- 추천 결과에 대한 리뷰 및 피드백
- 게시글 작성, 댓글, 좋아요 기능 제공

### 📌 **4. 사용자 인증 및 관리**
- JWT 기반 안전한 사용자 인증 시스템
- 사용자 프로필 및 선호도 관리
- Spring Security를 활용한 보안 강화

---

## 🛠 **기술 스택**
| 구분 | 기술 |
|------|------|
| **Frontend** | HTML, CSS, JavaScript, Thymeleaf |
| **Backend** | Java 17, Spring Boot 3.3.4, Spring Security, Spring Batch |
| **AI/ML** | Python, Flask, 머신러닝 알고리즘 |
| **Database** | MySQL, JPA, Hibernate |
| **API Documentation** | Swagger (SpringDoc) |
| **Authentication** | JWT (JSON Web Token) |
| **Build Tool** | Gradle |
| **Version Control** | Git, GitHub |

---

## 🏗 **시스템 아키텍처**

![시스템 아키텍처](https://github.com/user-attachments/assets/65fda520-735c-4783-9976-54d5252fa41c)

### 🔹 아키텍처 설명
- **Spring Boot 백엔드**: 주류 데이터 관리, 사용자 인증, API 제공
- **Flask AI 서버**: 머신러닝 기반 추천 알고리즘 처리
- **MySQL 데이터베이스**: 주류 정보, 사용자 데이터, 커뮤니티 데이터 저장
- **외부 API 연동**: 날씨 정보 실시간 연동

---

## 📂 **프로젝트 구조**
```
CheersMate
│── src
│   ├── main
│   │   ├── java/CheersMate/cheersmate
│   │   │   ├── config         # 프로젝트 설정 (보안, 웹, 스웨거 등)
│   │   │   ├── domain         # 핵심 비즈니스 로직
│   │   │   │   ├── controller # REST API 컨트롤러
│   │   │   │   ├── dto        # 데이터 전송 객체
│   │   │   │   ├── entity     # DB 엔티티 (주류, 음식, 커뮤니티 등)
│   │   │   │   ├── repository # 데이터 접근 계층
│   │   │   │   ├── service    # 비즈니스 로직 처리
│   │   │   │   ├── flask      # Flask AI 서버 연동 로직
│   │   │   │   └── mapper     # DTO-엔티티 변환 매퍼
│   │   │   ├── security       # 보안 관련 설정
│   │   │   ├── jwt            # JWT 인증 처리
│   │   │   ├── weather        # 날씨 API 연동 모듈
│   │   │   ├── exception      # 예외 처리
│   │   │   ├── response       # API 응답 포맷 정의
│   │   ├── resources
│   │   │   ├── application.yml # 환경 설정 파일
│   │   │   ├── static         # 정적 리소스 (CSS, JS, 이미지 등)
│   │   │   ├── templates      # Thymeleaf 템플릿
│   ├── test                   # 단위 및 통합 테스트
│── build.gradle               # 프로젝트 의존성 관리
```

---

## 📚 **API 문서**
- Swagger UI를 통해 REST API 문서 자동화
- API 엔드포인트 테스트 및 문서 확인: `/swagger-ui.html`

---

## 🚀 **실행 방법**
1. **환경 요구사항**
   - JDK 17
   - MySQL
   - Python 3.8+ (AI 서버용)

2. **백엔드 서버 실행**
   ```bash
   ./gradlew bootRun
   ```

3. **데이터베이스 설정**
   - application.yml 파일에서 데이터베이스 연결 설정

4. **AI 서버 실행** (별도 리포지토리)
   ```bash
   python app.py
   ```

