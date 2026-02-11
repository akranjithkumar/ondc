# Unified Vendor Control Platform for ONDC Q-Commerce 🚀

> **Team Algorithm Avengers** > *Dev Arena | Department of Information Technology*

A vendor-centric middleware platform acting as the single control layer between ONDC seller apps and vendor operations, transforming vendors from passive order receivers into active decision-makers.

---

## 📖 Table of Contents
- [Problem Statement](#-problem-statement)
- [Solution Overview](#-solution-overview)
- [Key Capabilities](#-key-capabilities)
- [System Architecture](#-system-architecture)
- [Sustainable Development Goals (SDGs)](#-sustainable-development-goals-sdgs)
- [Challenges & Learnings](#-challenges--learnings)
- [Future Scope](#-future-scope)
- [Getting Started](#-getting-started)
- [Team](#-team)

---

## 🚩 Problem Statement

**Fragmented Vendor Operations in ONDC Q-Commerce**

Vendors currently operate on multiple ONDC seller apps simultaneously, leading to several critical issues:
* **Siloed Inventory:** Inventory is maintained separately per seller app.
* **Synchronization Lag:** A sale on one app does not reflect instantly on others.
* **Operational Chaos:** Leads to overselling, frequent order cancellations, and manual stock reconciliation.
* **Lack of Control:** Vendors lack centralized visibility and decision support for accepting/rejecting orders, especially during peak demand.

---

## 💡 Solution Overview

Our solution is a **Vendor-Centric Web Application** that sits between ONDC Seller Apps and Vendor Operations.

It acts as a **Network-Agnostic Layer**, meaning it works with multiple seller apps simultaneously. Unlike standard seller apps, this system is built exclusively for vendors, giving them authority over their inventory and order flow.

### Core Value Proposition
* **Single Source of Truth:** Centralized inventory management prevents cross-app overselling.
* **Intelligent Decision Engine:** Prioritizes orders based on load, distance, and SLA.
* **Active Decision Making:** Transforms vendors from passive participants to active controllers of their digital commerce operations.

---

## ✨ Key Capabilities

1.  **Real-time Inventory Reservation:** Instantly locks inventory across all connected seller apps when an order is received on one.
2.  **Centralized Order Management:** A single dashboard to view, accept, reject, or partially fulfill orders from any ONDC source.
3.  **Smart Routing:** Automatically routes orders to the optimal outlet or dark store based on proximity and stock levels.
4.  **Health Monitoring:** Tracks the connectivity and health of linked Seller Apps.
5.  **Predictive Replenishment:** AI-driven alerts for low stock based on demand forecasting.
6.  **Offline Handling:** Fallback support via SMS/WhatsApp for vendors with poor connectivity.
7.  **Performance Tracking:** Monitors vendor reputation and operational metrics.

---

## 🏗 System Architecture

The platform operates as a middleware solution:

1.  **Vendor Layer:** Web Dashboard & Offline Interface (SMS/WhatsApp).
2.  **Control Layer (Middleware):**
    * *Inventory Engine:* Manages stock reservation.
    * *Decision Engine:* logic for priority, partial fulfillment, and routing.
    * *Forecasting Engine:* Demand prediction.
3.  **Integration Layer:** Connects to various ONDC Seller Apps via APIs.

*(Note: Add your specific tech stack details below)*

### Tech Stack
* **Frontend:** React.js / Next.js (Suggested)
* **Backend:** Node.js / Python (Suggested)
* **Database:** MongoDB / PostgreSQL (Suggested)
* **ONDC Protocol:** Beckn Protocol compliant adapters
* **Notifications:** Twilio / WhatsApp Business API

---

## 🌍 Sustainable Development Goals (SDGs)

This project directly contributes to the following UN SDGs:

* **SDG 8: Decent Work & Economic Growth**
    * Improves vendor productivity through automation.
    * Enables small vendors to compete with large platforms.
* **SDG 9: Industry, Innovation & Infrastructure**
    * Builds resilient digital infrastructure for the ONDC vendor ecosystem.
    * Introduces intelligent middleware for decentralized commerce.
* **SDG 12: Responsible Consumption & Production**
    * Minimizes inventory wastage and stock expiry through predictive forecasting.
    * Reduces carbon footprint by optimizing delivery routing and reducing returns.

---

## 🧠 Challenges & Learnings

### Challenges
* **Concurrency:** Preventing race conditions when multiple orders hit different seller apps simultaneously.
* **Compliance:** Designing partial fulfillment flows that strictly adhere to ONDC protocols.
* **Latency:** Ensuring decision-making happens in milliseconds for Q-Commerce standards.
* **Adoption:** creating a UI simple enough for vendors with limited digital literacy.

### Learnings
* **Inventory Authority:** Vendor-level inventory must be the absolute source of truth, not the seller app's database.
* **UX is Key:** Simplicity in the user interface is as critical as the backend logic for real-world adoption.
* **Offline First:** SMS/WhatsApp fallback is essential for reliability in unreliable network conditions.

---

## 🔮 Future Scope

* **AI Dynamic Pricing:** Real-time margin optimization based on demand.
* **Fraud Detection:** Advanced models to predict return fraud.
* **Inter-Vendor Sharing:** Enabling vendors to share inventory with each other to fulfill orders.
* **Hyperlocal Logistics:** Deep integration with diverse logistics partners.

**Long-Term Vision:** To become the default **Vendor Operating System** powering scalable, fair, and efficient digital commerce on ONDC.

---

## 🚀 Getting Started

*(Adjust the following commands based on your actual repo structure)*

### Prerequisites
* Node.js (v16+) / Python (3.8+)
* MongoDB / PostgreSQL
* ONDC Seller App Credentials

### Installation

1.  **Clone the repository**
    ```bash
    git clone [https://github.com/akranjithkumar/ondc.git](https://github.com/akranjithkumar/ondc.git)
    cd ondc
    ```

2.  **Install Dependencies**
    ```bash
    # For Backend
    cd server
    npm install
    
    # For Frontend
    cd ../client
    npm install
    ```

3.  **Environment Setup**
    * Create a `.env` file in the root directory.
    * Add your ONDC BAP/BPP keys, Database URL, and SMS Gateway keys.

4.  **Run the Application**
    ```bash
    # Start Backend
    npm run server
    
    # Start Frontend
    npm run client
    ```

---

## 👥 Team Algorithm Avengers

* **Sai Vijay Ragav M**
* **Jaswanth Prasanna V**
* **Ranjith Kumar A K**
* **Nitheesh Kumar R**

---

Made with ❤️ for **ONDC Q-Commerce**
