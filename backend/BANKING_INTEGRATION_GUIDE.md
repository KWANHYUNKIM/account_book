# 은행/카드사 API 연동 가이드

## 개요

현재 구현된 구조는 Mock 데이터를 사용하는 기본 프레임워크입니다. 실제 은행/카드사 API와 연동하려면 아래 가이드를 참고하세요.

## 1. 오픈뱅킹 API 연동 (한국)

### 1.1 한국금융결제원 오픈뱅킹

**공식 문서**: https://www.openbanking.or.kr/

**개인 개발자 사용 가능 여부:**
- ✅ **개인 개발자도 사용 가능합니다**
- 회원가입 및 앱 등록 후 승인 절차 필요
- 초기에는 테스트 환경(샌드박스)에서 개발 가능

**등록 절차:**
1. 오픈뱅킹 포털(https://www.openbanking.or.kr) 회원가입
2. 개발자 정보 등록 (개인/법인 구분)
3. 앱 등록 및 승인 신청
4. Client ID, Client Secret 발급
5. 테스트 환경에서 개발 시작
6. 운영 환경 전환 시 추가 승인 필요

**필요한 것:**
- 오픈뱅킹 개발자 등록
- Client ID, Client Secret 발급
- OAuth 2.0 인증 구현
- 앱 승인 (개인 프로젝트의 경우 간단한 승인 절차)

**주요 API 엔드포인트:**
- 인증: `https://openapi.openbanking.or.kr/oauth/2.0/authorize`
- 토큰: `https://openapi.openbanking.or.kr/oauth/2.0/token`
- 계좌 조회: `https://openapi.openbanking.or.kr/v2.0/account/list`
- 거래 내역: `https://openapi.openbanking.or.kr/v2.0/account/transaction_list`

**구현 예시 (`OpenBankingService.java`):**

```java
@Value("${openbanking.client-id}")
private String clientId;

@Value("${openbanking.client-secret}")
private String clientSecret;

@Value("${openbanking.redirect-uri}")
private String redirectUri;

public String getAuthorizationUrl(String bankCode) {
    return String.format(
        "https://openapi.openbanking.or.kr/oauth/2.0/authorize?" +
        "response_type=code&" +
        "client_id=%s&" +
        "redirect_uri=%s&" +
        "scope=login inquiry transfer&" +
        "state=%s&" +
        "auth_type=0&" +
        "bank_tran_id=%s",
        clientId, redirectUri, UUID.randomUUID(), generateBankTranId()
    );
}

public void handleOAuthCallback(Long accountId, String authorizationCode) {
    // 토큰 교환 API 호출
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("code", authorizationCode);
    params.add("client_id", clientId);
    params.add("client_secret", clientSecret);
    params.add("redirect_uri", redirectUri);
    params.add("grant_type", "authorization_code");
    
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
    ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
        "https://openapi.openbanking.or.kr/oauth/2.0/token",
        request,
        TokenResponse.class
    );
    
    // 토큰 저장
    BankAccount account = bankAccountRepository.findById(accountId).orElseThrow();
    account.setAccessToken(response.getBody().getAccessToken());
    account.setRefreshToken(response.getBody().getRefreshToken());
    account.setTokenExpiresAt(LocalDateTime.now().plusSeconds(response.getBody().getExpiresIn()));
    bankAccountRepository.save(account);
}

public void syncTransactions(Long accountId) {
    BankAccount account = bankAccountRepository.findById(accountId).orElseThrow();
    
    // 거래 내역 조회 API 호출
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(account.getAccessToken());
    headers.set("Content-Type", "application/json");
    
    // 요청 파라미터 구성
    Map<String, String> params = new HashMap<>();
    params.put("bank_tran_id", generateBankTranId());
    params.put("fintech_use_num", account.getAccountNumber());
    params.put("inquiry_type", "A");
    params.put("inquiry_base", "D");
    params.put("from_date", LocalDate.now().minusDays(30).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
    params.put("to_date", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
    
    HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);
    ResponseEntity<TransactionListResponse> response = restTemplate.postForEntity(
        "https://openapi.openbanking.or.kr/v2.0/account/transaction_list",
        request,
        TransactionListResponse.class
    );
    
    // 응답 파싱하여 Transaction 저장
    // ...
}
```

## 2. 카드사 API 연동

### 2.1 주요 카드사 API

각 카드사마다 API가 다르므로 개별적으로 구현해야 합니다.

#### 신한카드
- 개발자 센터: https://developers.shinhancard.com/
- OAuth 2.0 인증 필요
- 거래 내역 조회 API 제공

#### KB국민카드
- 개발자 센터: https://developers.kbcard.com/
- API 키 발급 필요

#### 하나카드
- 개발자 센터: https://developers.hanacard.co.kr/
- OAuth 인증 필요

### 2.2 구현 예시 (`CardApiService.java`)

```java
// 카드사별로 다른 구현 필요
public void syncTransactions(Long accountId) {
    BankAccount account = bankAccountRepository.findById(accountId).orElseThrow();
    
    // 카드사별 API 호출
    String cardCompany = account.getBankName();
    
    switch (cardCompany) {
        case "신한카드":
            syncShinhanCard(account);
            break;
        case "KB국민카드":
            syncKBCard(account);
            break;
        case "하나카드":
            syncHanaCard(account);
            break;
        default:
            throw new RuntimeException("지원하지 않는 카드사입니다: " + cardCompany);
    }
}

private void syncShinhanCard(BankAccount account) {
    // 신한카드 API 호출 로직
    // ...
}
```

## 3. 설정 파일 추가

`application.properties`에 다음 설정 추가:

```properties
# 오픈뱅킹 설정
openbanking.client-id=your-client-id
openbanking.client-secret=your-client-secret
openbanking.redirect-uri=http://localhost:8100/api/bank-accounts/{id}/openbanking/callback

# 카드사 API 설정
shinhancard.api-key=your-api-key
kbcards.api-key=your-api-key
hanacard.api-key=your-api-key
```

## 4. 보안 고려사항

1. **토큰 암호화 저장**: `accessToken`, `refreshToken`은 암호화하여 저장
2. **HTTPS 사용**: 모든 API 통신은 HTTPS 필수
3. **토큰 갱신**: 만료 전 자동 갱신 로직 구현
4. **에러 처리**: API 호출 실패 시 적절한 에러 처리

## 5. 테스트

실제 API 연동 전에 Mock 데이터로 테스트:
1. 계좌 등록
2. OAuth 인증 (Mock)
3. 거래 내역 동기화 (Mock)
4. 실제 API로 교체

## 6. 참고 자료

- 한국금융결제원 오픈뱅킹: https://www.openbanking.or.kr/
- 금융감독원 오픈API 가이드: https://www.fss.or.kr/
- 각 카드사 개발자 센터

