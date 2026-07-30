# Travel Compass 🧭

여행지 국가 정보(환율, 시차, 기후), 즐겨찾기, 여행 준비물 체크리스트, 예산 계산기, 여행 리뷰를 한 곳에서 제공하는 여행 정보 통합 웹 서비스입니다.

## 주요 기능

- **국가 검색/상세 정보**: 국가명(한글/영문)으로 검색하고, 국가의 기본 정보와 실시간 환율, 시차, 평균 기후 정보를 한 번에 확인
- **지도 기반 홈 화면**: 세계지도를 통해 국가를 시각적으로 탐색
- **즐겨찾기**: 관심 있는 국가를 즐겨찾기로 등록/조회/삭제
- **여행 준비물 체크리스트**: 개인별 준비물 목록 관리
- **여행 예산 계산기**: 환율 기반 예산 계산
- **여행 리뷰**: 국가별 여행 리뷰 작성 및 댓글
- **회원 관리**: 회원가입/로그인 (Spring Security 기반 세션 인증)

## 기술 스택

| 구분 | 기술 |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 3.5 (Web MVC, WebFlux) |
| View | Thymeleaf |
| Persistence | MyBatis, MySQL |
| Security | Spring Security (세션 기반 인증) |
| Build | Gradle |
| Test | JUnit 5, Mockito |
| 외부 연동 | Frankfurter API(환율), Open-Meteo API(기후), Wikidata/Wikipedia API |

## 아키텍처 개요

- **Controller**: REST API(`@RestController`)와 화면 라우팅(`@Controller`, `PageController`)을 분리
- **Facade 패턴**: `CountryFacadeService`가 환율/시차/기후/즐겨찾기 서비스를 조합해 국가 상세 정보를 하나의 응답으로 제공
- **공통 응답 규격**: 모든 REST API는 `ApiResponse<T>`로 응답을 감싸서 반환
- **예외 처리**: `BusinessException` + `GlobalExceptionHandler`로 일관된 에러 응답 처리
- **외부 API 클라이언트**: `WebClient` 기반으로 국가/환율/기후/타임존 관련 외부 API 연동을 클라이언트 단위로 분리

## 트러블슈팅

### 1. 무료 API의 사용 제약 발생

**문제 상황**

- `restcountries.com` API의 정책 변경으로 호출 제한 가능성이 생김
- 인증 필수화, 트래픽 제한이 발생할 여지가 있어 서비스 안정성에 리스크로 작용

**해결 방법**

- 국가명, 수도, 대륙, 통화 코드, 전화번호 부호 등 자주 변하지 않는 국가 기본 정보를 프로젝트 내부에 정적 JSON(`countries.json`)으로 내장
- 외부 API 호출 없이 로컬 데이터를 읽어오는 방식으로 전환

**효과**

- 외부 서버 장애나 정책 변경에도 앱이 100% 정상 작동
- 변하지 않는 정적 데이터를 매번 외부 API로 조회하는 데 드는 네트워크 비용과 장애 리스크를 제거

### 2. 국가 코드 대소문자 불일치로 인한 즐겨찾기 조회 실패

**문제 상황**

- 즐겨찾기 여부 확인 시, 입력받은 국가 코드(소문자)를 그대로 사용하여 대문자로 저장된 DB 데이터와 일치하지 않아 조회가 실패하는 버그 발생

**해결 방법**

- 입력받은 2자리 국가 코드를 대문자로 통일해 DB를 조회/저장하도록 수정
- Mockito를 활용한 단위 테스트로 정상 동작을 검증한 뒤 최종 적용

## 실행 방법

```bash
# 로컬 프로필로 실행 (기본값)
./gradlew bootRun

# 프로덕션 프로필로 실행
./gradlew bootRun --args='--spring.profiles.active=prod'
```

애플리케이션 실행 시 `schema.sql`, `data.sql`이 자동으로 적용됩니다.
