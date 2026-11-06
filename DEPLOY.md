# 🚀 Hướng dẫn Deploy MangaSVN lên Server

## Bước 1: Rotate Cloudflare R2 Keys (BẮT BUỘC)

Key cũ đã bị lộ. Vào Cloudflare dashboard:
1. **Cloudflare Dashboard** → R2 → **Manage R2 API Tokens**
2. Xóa / revoke token cũ
3. Tạo token mới → copy Access Key ID và Secret Access Key
4. Cập nhật vào file `.env` (hoặc biến môi trường trên server)

---

## Bước 2: Tạo JWT Secret mới

Chạy lệnh sau để tạo secret ngẫu nhiên 64 ký tự:

```bash
# Linux/Mac
openssl rand -hex 32

# Windows PowerShell
-join ((1..64) | ForEach-Object { '{0:x}' -f (Get-Random -Max 16) })
```

Copy kết quả vào `JWT_SECRET_KEY` trong `.env`.

---

## Bước 3: Chuẩn bị file `.env` trên server

Tạo file `.env` trên server (KHÔNG commit lên git):

```env
MYSQL_ROOT_PASSWORD=<mật_khẩu_mạnh_ngẫu_nhiên>
SPRING_DATASOURCE_PASSWORD=<same_as_above>
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/mangasvn

JWT_SECRET_KEY=<kết_quả_openssl_rand>

CLOUDFLARE_ACCESS_KEY=<key_mới_từ_cloudflare>
CLOUDFLARE_SECRET_KEY=<secret_mới_từ_cloudflare>
CLOUDFLARE_BUCKET_NAME=comics
CLOUDFLARE_UPLOAD_ENDPOINT=https://<account_id>.r2.cloudflarestorage.com
CLOUDFLARE_PUBLIC_URL=https://pub-<id>.r2.dev
```

---

## Bước 4: Deploy với Docker Compose

```bash
# Trên server
git pull
docker compose up -d --build
```

---

## Bước 5: Cài Nginx + HTTPS

```bash
# Cài Nginx
sudo apt install nginx certbot python3-certbot-nginx -y

# Thêm rate limit zone vào /etc/nginx/nginx.conf (trong http block):
# limit_req_zone $binary_remote_addr zone=login_zone:10m rate=10r/m;

# Copy config
sudo cp nginx/mangasvn.conf /etc/nginx/sites-available/mangasvn
sudo ln -s /etc/nginx/sites-available/mangasvn /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx

# Lấy SSL cert từ Let's Encrypt (miễn phí)
sudo certbot --nginx -d mangasvn.click -d www.mangasvn.click
```

---

## Bước 6: Đổi port app (tùy chọn)

Sau khi có Nginx, app không cần expose port 8080 ra ngoài nữa.
Trong `docker-compose.yml`, đổi:
```yaml
ports:
  - "8080:8080"
```
thành:
```yaml
ports:
  - "127.0.0.1:8080:8080"  # Chỉ Nginx trên localhost mới kết nối được
```
