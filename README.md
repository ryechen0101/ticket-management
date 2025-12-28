# Ticket Management System – Backend

一個以 **Spring Boot** 開發的工單管理系統後端 API，  
實作 JWT 認證、角色權限控管、工單流程狀態管理與歷史追蹤。

此專案用於展示我在 **Java Web / Spring Boot / REST API / 系統設計** 方面的能力。

---

## 系統角色與權限設計

| 角色 | 權限說明 |
|---|---|
| USER | 建立工單、管理自己的工單 |
| AGENT | 管理所有工單 |
| ADMIN | 使用者帳號管理（不處理工單） |

## 技術架構

- Java 17
- Spring Boot
- Spring Security + JWT
- Spring Data JPA (Hibernate)
- MySQL
- Maven
- Docker（Cloud Run 部署）
- RESTful API 設計

## 認證與授權（JWT）

- 登入成功後回傳 JWT
- JWT Payload 包含：
  - subject（username）
  - roles（USER / AGENT / ADMIN）
- 後端透過 Spring Security Filter 驗證 token
- Service 層再次進行業務權限檢查（避免越權）

## 📝 工單功能設計

### 工單狀態流程

OPEN → IN_PROGRESS → RESOLVED → CLOSED
（可 reopen）

狀態轉移會驗證合法性，避免不合理跳轉。

## 變更歷史與留言設計

- 所有重要欄位變更（狀態 / 優先級 / 標題 / 內容）皆記錄於 `TicketChangeHistory`
- 留言系統獨立於狀態變更
- 支援分頁查詢（最新 / 全部）

## 錯誤處理

- 自訂 Exception（BadRequest / Unauthorized / NotFound）
- 統一由 Global Exception Handler 處理
- API 回傳清楚錯誤訊息，方便前端顯示

## 環境設定（Secrets）

- JWT Secret / DB 帳密不寫死在程式碼
- 透過環境變數注入（適用 Cloud Run / Local）
- 專案僅提供範例設定檔，不包含敏感資訊

## 專案特色

- 前後端分離設計
- 明確角色權限與業務邏輯
- 非單純 CRUD，包含流程與歷史追蹤
- 可實際部署（Docker / Cloud Run）

## 前端 Demo

前端 UI 另有 GitHub Pages Demo：  
🔗 https://github.com/ryechen0101/ticket-management-ui


