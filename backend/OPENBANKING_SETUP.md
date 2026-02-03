# 오픈뱅킹 API 개인 개발자 등록 가이드

## 개인 개발자도 사용 가능합니다! ✅

한국금융결제원의 오픈뱅킹 API는 **개인 개발자도 사용할 수 있습니다**. 다만 등록 및 승인 절차가 필요합니다.

## 등록 절차

### 1단계: 오픈뱅킹 포털 가입

1. https://www.openbanking.or.kr 접속
2. "회원가입" 클릭
3. 개인 개발자 선택
4. 본인 인증 (휴대폰 인증 또는 공동인증서)

### 2단계: 개발자 정보 등록

- 이름, 이메일, 연락처 등 기본 정보 입력
- 개발 목적 및 용도 작성
- 약관 동의

### 3단계: 앱 등록

1. "앱 등록" 메뉴 선택
2. 앱 정보 입력:
   - 앱 이름: "가계부 애플리케이션"
   - 앱 설명: 개발 목적 및 기능 설명
   - 리다이렉트 URI: `http://localhost:8100/api/bank-accounts/{id}/openbanking/callback`
   - 사용 범위: 계좌 조회, 거래 내역 조회 등

### 4단계: 승인 신청

- 등록한 앱에 대해 승인 신청
- 개인 프로젝트의 경우 보통 1-3일 내 승인
- 승인 완료 시 Client ID, Client Secret 발급

### 5단계: API 키 발급

승인 완료 후:
- **Client ID**: API 호출 시 사용하는 식별자
- **Client Secret**: 보안을 위한 비밀키 (절대 노출 금지!)

## 테스트 환경

### 샌드박스 계정

- 초기 개발은 테스트 환경에서 진행
- 실제 계좌가 아닌 샌드박스 계정 사용
- 무료로 테스트 가능

### 테스트 환경 URL

```
인증: https://testapi.openbanking.or.kr/oauth/2.0/authorize
토큰: https://testapi.openbanking.or.kr/oauth/2.0/token
API: https://testapi.openbanking.or.kr/v2.0/...
```

## 운영 환경 전환

테스트 완료 후 운영 환경으로 전환하려면:
1. 운영 환경 앱 등록 신청
2. 추가 서류 제출 (필요 시)
3. 운영 환경 승인 대기
4. 운영 환경 API 키 발급

## 비용

- **개인 개발자**: 무료 (일일 API 호출 제한 있음)
- **상업적 이용**: 별도 계약 및 비용 발생 가능

## 주의사항

1. **Client Secret 보안**: 절대 코드에 하드코딩하지 말고 환경 변수로 관리
2. **토큰 관리**: Access Token은 만료 시간이 있으므로 자동 갱신 로직 필요
3. **API 호출 제한**: 일일 호출 횟수 제한이 있으므로 캐싱 전략 고려
4. **개인정보 보호**: 계좌 정보 등 민감 정보는 암호화하여 저장

## 실제 연동 예시

등록 완료 후 `application.properties`에 추가:

```properties
# 오픈뱅킹 설정 (테스트 환경)
openbanking.client-id=발급받은-client-id
openbanking.client-secret=발급받은-client-secret
openbanking.redirect-uri=http://localhost:8100/api/bank-accounts/{id}/openbanking/callback
openbanking.api-base-url=https://testapi.openbanking.or.kr
```

그리고 `OpenBankingService.java`의 Mock 부분을 실제 API 호출로 교체하면 됩니다.

## 참고 자료

- 오픈뱅킹 공식 포털: https://www.openbanking.or.kr/
- 개발자 가이드: https://www.openbanking.or.kr/guide/guide.do
- API 문서: https://www.openbanking.or.kr/apidoc/guide.do
- 샘플 코드: 오픈뱅킹 포털에서 제공

## 문의

등록 과정에서 문제가 발생하면:
- 오픈뱅킹 고객센터: 1588-5000
- 이메일: support@openbanking.or.kr


