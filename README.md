# 🌐 Core Web Crawler

A scalable, production-grade web crawler built in Java (Spring Boot) that crawls, parses, and stores structured metadata from any URL. Supports distributed crawling, fault tolerance, robots.txt respect, and dual storage (MySQL + MongoDB). Comes with a Streamlit-based UI for triggering crawls and viewing results.

---

## ✨ Features

- 🔁 **Batch URL ingestion** from MySQL
- 🌍 **Crawls any public URL** (HTML pages)
- ⚙️ **Retry & Dead Letter Queue** using Kafka
- 🧠 **Smart Parser** extracts title, description, text, meta tags, OpenGraph, schema.org, forms, images, links, etc.
- 🛢️ **Stores metadata**
  - Structured: MySQL
  - Raw + extended: MongoDB
- 🚫 **Respects robots.txt**
- 📜 **Schema-independent dumping** of all HTML metadata
- 📊 **Streamlit UI** to run crawler and see results
- ☁️ **Hosted on AWS EC2** for public access

---

## 🧱 Architecture Overview

```plaintext
MySQL (seed URLs) --> Spring Boot REST API --> Crawler
                             |                     |
                        Kafka (DLQ)            HTML Parser
                             |                     |
                          Streamlit UI        MongoDB + MySQL
