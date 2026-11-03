FROM eclipse-temurin:25-jdk AS java
FROM node:24-bookworm-slim AS wasm-build
COPY --from=java /opt/java/openjdk /opt/java/openjdk
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"
RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates curl libatomic1 unzip \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /workspace/ScheduleCompose
COPY ScheduleCompose/ .
ENV CI=true
RUN --mount=type=cache,target=/root/.cache/JetBrains/Kotlin ./kotlin build -m webApp

FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build
ARG BUILD_CONFIGURATION=Release
WORKDIR /src
COPY ["ScheduleServer/ScheduleServer.csproj", "ScheduleServer/"]
RUN dotnet restore "ScheduleServer/ScheduleServer.csproj"
COPY ScheduleServer/ ScheduleServer/
WORKDIR /src/ScheduleServer
RUN dotnet publish "ScheduleServer.csproj" -c $BUILD_CONFIGURATION -o /app/publish /p:UseAppHost=false

FROM mcr.microsoft.com/dotnet/aspnet:10.0 AS final
USER $APP_UID
WORKDIR /app
COPY --from=build /app/publish .
COPY --from=wasm-build /workspace/ScheduleCompose/build/tasks/_webApp_buildWasmJsAppWasmJsDebug/ ./wwwroot/
EXPOSE 8080
ENTRYPOINT ["dotnet", "ScheduleServer.dll"]
