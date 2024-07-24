# Agido Account Management System

## Description
The Agido Account Management System is designed to manage user accounts efficiently. It supports user registration, login, account creation, deposit and withdrawal transactions, user and service statistics, and transaction approvals. The system operates with two types of users: `USER` and `SERVICE`.

## Table of Contents
- [Usage](#usage)
- [Features](#features)

## Usage
### Test Data
The system is populated with the following test data upon initialization for ease of testing and overview:

| Username  | Password | Role    |
|-----------|----------|---------|
| User_1    | u111     | USER    |
| User_2    | u222     | USER    |
| User_3    | u333     | USER    |
| Service_1 | s111     | SERVICE |

### User Actions
1. **Register and Login**: Users can register and log in to the system.
2. **First Login**: On the first login, users are redirected to a create account page to input the initial balance.
3. **Subsequent Logins**: Users are redirected to their account overview page on subsequent logins.
4. **Transactions**: Users can deposit and withdraw from their account. Withdrawals require approval from a service user.
5. **Statistics**: Users can view account related statistics about total deposits and withdrawals between two dates.

### Service User Actions
1. **Register and Login**: Users can register and log in to the system.
2. **Dashboard**: Service users have a dashboard page after logging in.
3. **Authorize Transactions**: Service users can authorize pending transactions.
4. **Service Statistics**: Service users can view a list of all accounts sorted by balance and query statistics about all deposits and withdrawals within a specified time interval.

## Features
- User Registration and Login
- Account Balance Management
- Deposit and Withdrawal Transactions
- Transaction Approval by Service Users
- User and Service Statistics
