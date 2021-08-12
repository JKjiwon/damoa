# Damoa API

## 소개
- Band.us 와 같은 모임형 커뮤니티
- JPA 성능 최적화
- 꾸준한 테스트 작성 (Jacoco 기준: branch coverage, instruction coverage 80% 이상 유지)
- RestDocs를 통한 API 문서 작성

## 

## 기술 스택
- Java
- Spring Boot
- JPA
- Spring Security - JWT Token
- Spring Rest Docs
- Gradle
- MYSQL
- AWS

## API 문서
https://jkjiwon.github.io/damoa-api-guide/

## Wiki 
1. [기능 정의](https://github.com/JKjiwon/damoa/wiki/%5B1%5D.-기능-정의)
2. [JPA 성능 최적화 전략](https://github.com/JKjiwon/damoa/wiki/%5B2%5D.-JPA-성능-최적화-전략)
3. [Database ERD](https://github.com/JKjiwon/damoa/wiki/%5B3%5D.-DB-ERD)
4. [Git 커밋 메세지](https://github.com/JKjiwon/damoa/wiki/%5B4%5D.-Git-커밋-메세지)

## 실행 환경
1. lombok 설치
2. application.yml 에 파일 저장 위치 변경(damoa.file.image.upload.path)
3. application.yml 에 로컬 DB를 등록

## 빌드 후 실행
1. 콘솔로 이동
2. ./gradlew build
3. cd build/libs
4. java -jar damoa-0.0.1-SNAPSHOT.jar
5. 확인

## JaCoCo Test Report
branch coverage, instruction coverage 80% 이상 유지
![JacocoTestResult](github-images/jacoco_test.png)