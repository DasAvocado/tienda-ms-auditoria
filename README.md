# ms-auditoria

Microservicio propio: guarda TODOS los eventos de RabbitMQ (binding `#`) para trazabilidad.

- Puerto: **8085** · Java 21 · Spring Boot 4.1.1
- Seguridad: valida los JWT de Microsoft Entra ID (firma, vigencia, issuer, audience), scope `Menu-compra` y roles `CLIENTE` / `ADMIN`.
- Mensajería: exchange `pedidos360.events` (RabbitMQ).

## Endpoints
| Método y ruta | Acceso |
|---|---|
| `GET /api/auditoria` (últimos 100) · `GET /api/auditoria/resumen` | ADMIN |

## Ejecutar en local
Requisitos: JDK 21 y Postgres y RabbitMQ corriendo (repo `infra`: `docker compose up -d`).

```bash
# Linux / macOS
./mvnw spring-boot:run
# Windows (PowerShell)
.\mvnw.cmd spring-boot:run
```
(o abre el proyecto en IntelliJ y ejecuta la clase `*Application`). Sin variables de entorno usa los valores por defecto de `application.yml` (localhost, `admin/password123`, `guest/guest`).

## Variables de entorno
Ver `.env.example`. Las principales: `RABBIT_HOST`, `DB_URL`, `DB_USER`, `DB_PASSWORD`, `AZURE_TENANT_ID`, `AZURE_CLIENT_ID`.

## Desplegar en su instancia EC2
```bash
# 1) Una sola vez: Docker + git + swap (t3.micro tiene solo 1 GB)
sudo fallocate -l 2G /swapfile && sudo chmod 600 /swapfile && sudo mkswap /swapfile && sudo swapon /swapfile
#    instala docker y el plugin "docker compose" segun tu AMI (Amazon Linux: sudo dnf install -y docker git)
# 2) Clonar y configurar
git clone https://github.com/<tu-usuario>/ms-auditoria.git && cd ms-auditoria
cp .env.example .env && nano .env        # INFRA = IP PRIVADA de la instancia de infraestructura
# 3) Construir y levantar
docker compose up -d --build
docker compose logs -f
```
Security Group: abrir el puerto **8085** (el API Gateway llama a la IP pública de esta instancia).
API Gateway: rutas `ANY /api/auditoria` y `ANY /api/auditoria/{proxy+}` → `http://<IP-ELASTICA>:8085/api/auditoria[/{proxy}]`.

## Subir a GitHub
```bash
git init && git add . && git commit -m "ms-auditoria" && git branch -M main
git remote add origin https://github.com/<tu-usuario>/ms-auditoria.git && git push -u origin main
```
