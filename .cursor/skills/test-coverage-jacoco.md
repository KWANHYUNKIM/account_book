# Test Coverage with JaCoCo

## 개요
JaCoCo를 사용하여 테스트 커버리지를 측정하고 관리합니다.

## 설정

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>PACKAGE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.70</minimum>
                            </limit>
                        </limits>
                    </rule>
                    <rule>
                        <element>CLASS</element>
                        <limits>
                            <limit>
                                <counter>BRANCH</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## 실행

```bash
# 테스트 실행 및 커버리지 리포트 생성
mvn clean test jacoco:report

# 커버리지 리포트 확인
# target/site/jacoco/index.html
```

## 커버리지 목표

```java
// Service 계층: 80% 이상
// Controller 계층: 70% 이상
// Repository 계층: 60% 이상 (쿼리 위주)
// 핵심 비즈니스 로직: 100% 목표
```

## 예외 처리

```xml
<!-- 특정 클래스 제외 -->
<configuration>
    <excludes>
        <exclude>**/config/**</exclude>
        <exclude>**/dto/**</exclude>
        <exclude>**/entity/**</exclude>
    </excludes>
</configuration>
```

## 베스트 프랙티스
- 최소 70% 커버리지 유지
- 핵심 비즈니스 로직은 100% 목표
- DTO, Entity는 제외 가능
- CI/CD에서 커버리지 체크 자동화

