# 프로젝트 아키텍처

## 📦 패키지 구조
이 프로젝트는 **클린 아키텍처(Clean Architecture)** 와 **계층-도메인 패키지** 구조로 설계되었습니다.

```
groupdeal
│
├── presentation          # 표현 계층
│   └── {domain}
│       ├── controller    # HTTP 요청/응답 처리
│       └── dto           # API 스팩(요청/응답 DTO)
│
├── application          # 응용 계층
│   └── {domain}
│       ├── facade       # 복잡한 워크플로우 조율
│       └── service      # 비즈니스 로직 처리
│
├── domain              # 도메인 계층
│   ├── common
│   └── {domain}
│       ├── model        # 도메인 모델
│       └── repository   # 저장소 인터페이스
│
├── infrastructure      # 인프라 계층
│   ├── common
│   └── {domain}
│       ├── entity       # JPA 엔티티
│       └── repository   # 저장소 구현체
│
├── external           # 외부 연동 계층
│   └── pg             # PG(결제) 연동
│       ├── portone
│       ├── toss
│       └── ...
│
└── global            # 전역 설정
    ├── config
    └── error
```

---

## 🎯 계층별 역할

### Presentation (표현 계층)
- **역할**: HTTP 요청/응답 처리, API 스펙 정의
- **의존**: Application 계층
- **특징**: 컨트롤러, DTO, 입력 검증

### Application (응용 계층)
- **역할**: 유스케이스 실행, 트랜잭션 관리, 여러 도메인 객체 조율
- **의존**: Domain 계층
- **특징**:
    - **Facade**: 여러 서비스들을 주입 받아서 처리, 트랜잭션을 하나로 묶어주는 역할
    - **Service**: 단순 비즈니스 로직

### Domain (도메인 계층)
- **역할**: 순수 비즈니스 규칙, 핵심 로직
- **의존**: 없음
- **특징**:
    - 순수 자바 클래스로 이루어져 있으며 프로젝트 전반에서 사용
    - 도메인 모델과 Repository 인터페이스

### Infrastructure (인프라 계층)
- **역할**: 기술적 구현 세부사항
- **의존**: Domain 계층 (인터페이스 구현)
- **특징**:
    - JPA 엔티티 (기술 종속적)
    - Repository 구현체 (Adapter 패턴)
    - Domain ↔ Entity 변환

### External (외부 연동 계층)
- **역할**: 외부 API 통신
- **특징**: 
    - PG, OAuth 등 외부 서비스 클라이언트
    - Mediator 패턴으로 여러 PG사 통합 관리


### Global (전역 설정)
- **역할**: 애플리케이션 전역 설정, 공통 예외 처리

---

## 🔄 의존성 방향

### 의존성 규칙
1. **외부 → 내부**: 외부 계층이 내부 계층을 의존
2. **Domain 독립**: Domain은 어떤 계층도 의존하지 않음
3. **의존성 역전**: Infrastructure가 Domain의 인터페이스를 구현

```
Presentation → Application → Domain ← Infrastructure
                    ↓
                 External
```
---