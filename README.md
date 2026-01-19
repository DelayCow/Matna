# Matna
# 🍳 레시피 및 공동구매 플랫폼 - 맛나
<p align="center"><img width="677" height="369" alt="matna_logo" src="https://github.com/user-attachments/assets/914ac58d-b6c9-432e-9aec-e60bae2c3131" /></p>

## 🍽️ 맛나 플랫폼 소개
맛나는 식재료 소비가 빠르지 않은 1인 가구를 위한 **식재료 공동구매**와 후기를 통한 대체재료 정보를 포함한 **레시피를 제공**하는 서비스입니다.<br>

## 🍽️ 프로젝트 기간 
**1차 springboot기반 구현: 2025.11.17 ~ 2025.12.05(3주)**<br/>
**2차 뷰 서버 분리(리액트) 리팩토링: 2025.12.17 ~ 2025.12.30(2주)**

## 🍽️ 팀 소개
| 지연우 | 박세희 | 심재용 | 황혁준 |
|:------:|:------:|:------:|:------:|
| <img src="https://avatars.githubusercontent.com/u/110551002?v=4" alt="지연우" width="150"> | <img src="https://avatars.githubusercontent.com/u/237663119?v=4" alt="박세희" width="170"> |<img src="https://avatars.githubusercontent.com/u/102509662?v=4" alt="심재용" width="170"> |<img src="https://avatars.githubusercontent.com/u/225848337?v=4" alt="황혁준" width="170"> |
| [@wldusdn](https://github.com/wldusdn) | [@shpark091](https://github.com/shpark091) | [@reeuse](https://github.com/reeuse) | [@CoffeCodeTV](https://github.com/CoffeCodeTV) |
| 팀장<br/>일정 및 업무분담(jira)<br/>시큐어코딩, 리액트구조설계<br/>홈&레시피api연동<br/>마이페이지 repository 구현 및 단위테스트 | 자료문서화<br/>외부api 연동(지도,S3)<br/>공동구매 api연동 및 repository 구현 및 단위테스트 | Git 관리, 시큐어코딩<br/>관리자 api연동 및 repository 구현 밎 단위 테스트 | 업무명세서관리<br/>마이페이지 service 구현 및 단위테스트   |

## 🍽️ 주요 기능 
- **🍕 레시피**
  -   **사용자 기반 대체재료 정보 제공**: 레시피 후기 등록 시 사용자가 실제 활용한 '대체 재료' 정보를 수집하여, 조회 시 함께 제공
  -   **공동구매 연계**: 레시피에 포함된 식재료가 현재 공동구매 진행 중인지 여부 노출
  -   **개인화 검색 및 필터링**: 사용자의 취향을 고려한 매운맛 단계별 필터링, 검색 기능 및 다양한 정렬 조건(최신순, 후기순 등)을 지원

- **🪙 공동구매**
  - **기간형 공동구매** : 정해진 기간 내 모집된 인원이 전체 비용을 균등하게 분할하여 참여하는 유연한 공동구매 모델
  - **수량형 공동구매** : 사용자가 원하는 필요 수량을 직접 선택하여 참여하며, 목표 수량 도달 시 체결되는 방식의 공동구매 모델

- **👮 관리자 기능**
  - **회원관리** : 서비스 가이드라인을 위반한 회원을 식별하고 활동 정지 등의 제재 조치를 관리
  - **재료관리** : 레시피와 공동구매 서비스의 기준이 되는 식재료를 표준화하여 관리
  - **공구관리** : 진행 중인 공동구매의 이상 징후 포착 시 즉시 중단시키거나 상태 제어
  - **신고관리** : 회원 및 게시물(공동구매) 관련 신고 접수 건을 검토 후 처리

## 🍽️ Software Architecture
- **1차**
<img width="949" height="483" alt="1차 SW 아키텍처" src="https://github.com/user-attachments/assets/93ea709c-4582-4196-9e36-e76307b1b25f" />

## 🍽️ ERD
<a href="https://www.erdcloud.com/d/JfxWN2RnTDHPESGw4">**🔗 ERD**</a>
<br />
<br />
<img width="1642" height="837" alt="스크린샷 2026-01-19 142001" src="https://github.com/user-attachments/assets/78a9482e-2e1e-4c22-8244-a3ec39aea3ed" />

## 🍽️ Figma
<a href="https://www.figma.com/design/K9scKLFKakhRm5riDAQbCc/2%EC%B0%A8%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8_UI%EB%AA%85%EC%84%B8%EC%84%9C?node-id=0-1&t=5Awv7foQqUBw2Kn6-1">**🔗 Figma**</a>
<br />
<br />
<img width="1400" height="849" alt="스크린샷 2026-01-19 142748" src="https://github.com/user-attachments/assets/c9a4c429-21dc-45b3-96e0-4afeaacbf7ff" />


## 🍽️ Git Branch & Commit Convention
### 🔹 Branch Naming
> 업무명(영문)을 기준으로 브랜치 생성

**예시**
```bash
Manager
Login
Homepage
MyPage
```

---
### 🔹 Commit Type

| Type         | 설명                  |
| ------------ | ------------------- |
| **feat**     | 새로운 기능 추가           |
| **fix**      | 버그 수정               |
| **style**    | UI 관련 수정            |
| **refactor** | 코드 구조 개선 (기능 변화 없음) |
| **test**     | 테스트 코드 추가 또는 수정     |
| **docs**     | 문서, 이미지 변경          |
| **revert**   | 이전 커밋 되돌림           |
| **delete**   | 코드 또는 파일 삭제         |
| **wip**      | 작업 중 또는 실험적 변경사항    |

---

### 🔹 Commit Message 규칙 - 영문

```
[Type]: [Message]
```

**예시**

```
feat: Added login
fix: Fixed appointment registration error
style: Improved manager main page UI
```
