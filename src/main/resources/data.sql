-- Inserting data into the accounts table
INSERT INTO accounts (balance, creation_date, user_id)
VALUES
    (1000, TIMESTAMP '2024-07-20 12:00:00', 1),
    (2000, TIMESTAMP '2024-07-21 12:00:00', 2),
    (3000, TIMESTAMP '2024-07-22 12:00:00', 3);

-- Inserting data into the transactions table
INSERT INTO transactions (amount, transaction_type, transaction_approval_status, account_id, transaction_date)
VALUES
    -- Transactions for first account
    (10, 'DEPOSIT', 0, 1, TIMESTAMP '2024-07-20 13:00:00'),
    (20, 'DEPOSIT', 0, 1, TIMESTAMP '2024-07-20 14:00:00'),
    (30, 'DEPOSIT', 0, 1, TIMESTAMP '2024-07-20 15:00:00'),
    (40, 'WITHDRAWAL', 1, 1, TIMESTAMP '2024-07-20 16:00:00'),
    (10, 'DEPOSIT', 0, 1, TIMESTAMP '2024-07-20 17:00:00'),

    -- Transactions for second account
    (10, 'DEPOSIT', 0, 2, TIMESTAMP '2024-07-21 13:00:00'),
    (20, 'DEPOSIT', 0, 2, TIMESTAMP '2024-07-21 14:00:00'),
    (50, 'WITHDRAWAL', 1, 2, TIMESTAMP '2024-07-21 15:00:00'),
    (30, 'DEPOSIT', 0, 2, TIMESTAMP '2024-07-21 16:00:00'),
    (10, 'WITHDRAWAL', 1, 2, TIMESTAMP '2024-07-21 17:00:00'),

    -- Transactions for third account
    (10, 'DEPOSIT', 0, 3, TIMESTAMP '2024-07-22 13:00:00'),
    (50, 'WITHDRAWAL', 1, 3, TIMESTAMP '2024-07-22 14:00:00'),
    (10, 'DEPOSIT', 0, 3, TIMESTAMP '2024-07-22 15:00:00'),
    (20, 'WITHDRAWAL', 1, 3, TIMESTAMP '2024-07-22 16:00:00'),
    (30, 'WITHDRAWAL', 1, 3, TIMESTAMP '2024-07-22 17:00:00'),
    (10, 'DEPOSIT', 0, 3, TIMESTAMP '2024-07-22 18:00:00');
