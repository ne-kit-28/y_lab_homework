# Frontend (Next.js) minimal SPA

- Default API base: http://backend:8080 when running in Docker Compose
- Pages:
  - / (login)
  - /dashboard (simple data view of habits)
- Start commands:
  - docker build -f frontend/Dockerfile -t habits-frontend .
  - docker run --rm -p 3000:3000 habits-frontend
