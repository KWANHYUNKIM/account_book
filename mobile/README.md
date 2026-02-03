# 가계부 모바일 앱

React Native로 개발된 안드로이드 및 iOS용 가계부 모바일 애플리케이션입니다.

## 필수 요구사항

### 공통
- Node.js 18 이상
- npm 또는 yarn

### Android 개발
- Android Studio
- Android SDK
- Java Development Kit (JDK)

### iOS 개발 (macOS만)
- Xcode
- CocoaPods

## 설치 및 실행

### 1. 의존성 설치

```bash
npm install
```

### 2. iOS 의존성 설치 (macOS만)

```bash
cd ios
pod install
cd ..
```

### 3. 개발 서버 시작

```bash
npm start
```

### 4. 앱 실행

**Android:**
```bash
npm run android
```

**iOS (macOS만):**
```bash
npm run ios
```

## API 연결 설정

모바일 앱은 백엔드 API를 호출합니다. `src/services/api.ts` 파일에서 API 주소를 설정할 수 있습니다.

### 개발 환경별 설정

- **Android 에뮬레이터**: `http://10.0.2.2:8080/api`
- **iOS 시뮬레이터**: `http://localhost:8080/api`
- **실제 기기**: 컴퓨터의 로컬 IP 주소 사용 (예: `http://192.168.0.100:8080/api`)

## 프로젝트 구조

```
mobile/
├── src/
│   ├── screens/          # 화면 컴포넌트
│   │   ├── HomeScreen.tsx
│   │   ├── TransactionFormScreen.tsx
│   │   └── TransactionListScreen.tsx
│   ├── services/        # API 서비스
│   │   └── api.ts
│   ├── types/           # TypeScript 타입
│   │   └── Transaction.ts
│   └── App.tsx          # 메인 앱 컴포넌트
├── android/             # Android 네이티브 코드
├── ios/                 # iOS 네이티브 코드
└── package.json
```

## 주요 기능

- 수입/지출 거래 추가, 수정, 삭제
- 거래 내역 조회 및 필터링
- 수입/지출 통계 및 잔액 표시
- 카테고리별 거래 관리

## 문제 해결

### Android 빌드 오류
- Android Studio에서 SDK를 최신 버전으로 업데이트
- `android/gradle.properties`에서 Java 버전 확인

### iOS 빌드 오류
- Xcode를 최신 버전으로 업데이트
- `pod install` 재실행
- Xcode에서 프로젝트를 열어서 서명 설정 확인

### API 연결 실패
- 백엔드가 실행 중인지 확인
- 방화벽 설정 확인
- 실제 기기 사용 시 같은 네트워크에 연결되어 있는지 확인


