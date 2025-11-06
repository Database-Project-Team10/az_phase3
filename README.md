# 2025-2 DB 팀 프로젝트 10팀 리포지토리

## 개발 환경
- JDK 11(수업 내 권장 버전)
- oracle 21c
- ojdbc 11

## 설명
- 이 프로젝트는 Web 기반 프로젝트를 하기 직전 phase로, CLI 환경에서 JDBC를 통해 DB와의 통신을 원활히 할 수 있는지 검증하는 미니 프로젝트입니다.
- 이후 JSP 혹은 Thymeleaf로의 확장의 편리함을 위해 레이어를 나누어 MVC 패턴을 적용했습니다.

## 디렉토리 구조
- 도메인 별로 분류합니다. 3가지 레이어로 구분되며 아래와 같습니다.
  - Controller
    - 사용자에게 메뉴를 보여주고, 입력을 받는 레이어입니다. (User Interface)
  - Service
    - 주요 비즈니스 로직들이 위치하는 레이어입니다.
  - Repository
    - DB와 직접 연결되는 레이어이며, JDBC를 활용합니다.

## 주의 사항
- jdbc 드라이버의 경우 직접 설치하여 적용해야합니다.
- [oracle jdbc 드라이버 설치 링크](https://www.oracle.com/kr/database/technologies/appdev/jdbc-downloads.html)
- 소스 코드를 제외한 모든 생성된 파일들은 .gitignore에 포함시켜 리포지토리에 push하지 않습니다.

## 개발 방법
- 과제를 위한 미니 프로젝트이므로 branch 전략은 따로 사용하지 않습니다.
- 아래 2가지 방법 중 어떤 것을 사용해도 무방합니다. (다만 충돌에는 유의하여야 합니다.)
    1.  fork 후 PR 방식을 사용
    2.  원격 저장소를 직접 로컬 저장소에 가져간 후 master에 push

## 📊 D to B 팀원
| <img src="https://avatars.githubusercontent.com/u/108778962?v=4" width="150" height="150"/> |<img src="https://avatars.githubusercontent.com/u/130034324?v=4" width="150" height="150"/>|<img src="https://avatars.githubusercontent.com/u/172799476?v=4" width="150" height="150"/>|
|:-------------------------------------------------------------------------------------------:|:-:|:-:|
|                           김채은<br/>[@Chaechaekim](https://github.com/Chaechaekim)                           |                       전창우<br/>[@JEONW00](https://github.com/JEONW00)                        |윤강훈<br/>[@YoonGangHoon](https://github.com/YoonGangHoon)|
