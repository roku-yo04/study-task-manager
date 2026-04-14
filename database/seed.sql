-- ============================================
-- Study Task Manager - Database Schema
-- SQL Server
-- Version: 1.0
-- Date: 2024-12-24
-- ============================================

-- Create database
CREATE DATABASE study_task_manager;
GO

USE study_task_manager;
GO

-- ============================================
-- Drop existing tables (for clean setup)
-- ============================================
IF OBJECT_ID('tasks', 'U') IS NOT NULL DROP TABLE tasks;
IF OBJECT_ID('categories', 'U') IS NOT NULL DROP TABLE categories;
IF OBJECT_ID('user_roles', 'U') IS NOT NULL DROP TABLE user_roles;
IF OBJECT_ID('roles', 'U') IS NOT NULL DROP TABLE roles;
IF OBJECT_ID('users', 'U') IS NOT NULL DROP TABLE users;
GO

-- ============================================
-- Table: users
-- Description: Store user account information
-- ============================================
CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL,
    email NVARCHAR(100) NOT NULL,
    password_hash NVARCHAR(255) NOT NULL,
    display_name NVARCHAR(100),
    is_active BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    
    -- Constraints
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email UNIQUE (email)
);




-- Indexes for faster queries
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
GO

-- ============================================
-- Table: roles
-- Description: Store user roles (USER, ADMIN)
-- ============================================
CREATE TABLE roles (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(30) NOT NULL,
    
    -- Constraints
    CONSTRAINT uq_roles_name UNIQUE (name)
);

-- Insert default roles
INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (name) VALUES ('ADMIN');
GO

-- ============================================
-- Table: user_roles
-- Description: Many-to-many relationship between users and roles
-- ============================================
CREATE TABLE user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    
    -- Composite Primary Key
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    
    -- Foreign Keys
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) 
        REFERENCES roles(id) ON DELETE CASCADE
);
GO

-- ============================================
-- Table: categories
-- Description: Store task categories (per user)
-- ============================================
CREATE TABLE categories (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL,
    color NVARCHAR(7) NOT NULL DEFAULT '#3357FF',
    user_id INT NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
	
    CONSTRAINT fk_categories_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
	-- ràng buộc
    CONSTRAINT uq_categories_user_name UNIQUE (user_id, name)
);
GO

-- Index for faster queries
CREATE INDEX idx_categories_user_id ON categories(user_id);
GO

-- ============================================
-- Table: tasks
-- Description: Store user tasks
-- ============================================
CREATE TABLE tasks (
    id INT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(200) NOT NULL,
    description NVARCHAR(MAX) NULL, -- Mô tả cho tasks
    status NVARCHAR(20) NOT NULL,
    priority NVARCHAR(20) NOT NULL,
    due_date DATE NULL,
    user_id INT NOT NULL,
    category_id INT NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT chk_tasks_status 
        CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE')),

    CONSTRAINT chk_tasks_priority 
        CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),

    CONSTRAINT fk_tasks_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT fk_tasks_category FOREIGN KEY (category_id)
        REFERENCES categories(id) ON DELETE NO ACTION
		-- khi xóa categories thì task:Uncategorized 
);
GO

-- Indexes for faster queries
CREATE INDEX idx_tasks_user_id ON tasks(user_id);
CREATE INDEX idx_tasks_category_id ON tasks(category_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
GO

-- ============================================
-- Sample Data (for testing)
-- ============================================

-- Create test user
-- Note: password_hash is temporary, will be replaced by BCrypt hash in backend
INSERT INTO users (username, email, password_hash, display_name, is_active) 
VALUES 
    ('testuser', 'test@example.com', 'temp_password_will_be_hashed', 'Test User', 1),
    ('admin', 'admin@example.com', 'temp_password_will_be_hashed', 'Admin User', 1);
GO

-- Assign roles
-- testuser = USER role
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);

-- admin = USER + ADMIN roles
INSERT INTO user_roles (user_id, role_id) VALUES (2, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2);
GO

-- Create sample categories for testuser
INSERT INTO categories (name, color, user_id) 
VALUES 
    ('Work', '#FF5733', 1),
    ('Personal', '#33FF57', 1),
    ('Study', '#3357FF', 1),
    ('Health', '#F39C12', 1);
GO

-- Create sample tasks for testuser
INSERT INTO tasks (title, description, status, priority, due_date, user_id, category_id)
VALUES 
    ('Complete project proposal', 'Write and submit the Q1 project proposal to management', 'TODO', 'HIGH', '2024-12-31', 1, 1),
    ('Team meeting', 'Weekly sync with development team', 'TODO', 'MEDIUM', '2024-12-26', 1, 1),
    ('Buy groceries', 'Milk, eggs, bread, vegetables', 'TODO', 'LOW', '2024-12-25', 1, 2),
    ('Study Spring Security', 'Complete authentication and authorization modules', 'IN_PROGRESS', 'HIGH', '2024-12-30', 1, 3),
    ('Learn React Hooks', 'Practice useState, useEffect, useContext', 'IN_PROGRESS', 'MEDIUM', '2024-12-28', 1, 3),
    ('Morning jog', 'Run 5km in the park', 'DONE', 'MEDIUM', '2024-12-24', 1, 4),
    ('Code review', 'Review pull requests from team members', 'DONE', 'HIGH', '2024-12-23', 1, 1),
    ('Prepare presentation', 'Create slides for client meeting', 'TODO', 'HIGH', '2024-12-27', 1, 1);
GO

-- ============================================
-- Verification Queries
-- ============================================

-- Check all tables
SELECT TABLE_NAME 
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_NAME;
GO

-- Check users
SELECT id, username, email, display_name, is_active, created_at 
FROM users;
GO

-- Check roles
SELECT * FROM roles;
GO

-- Check user_roles
SELECT u.username, r.name as role_name
FROM user_roles ur
JOIN users u ON ur.user_id = u.id
JOIN roles r ON ur.role_id = r.id;
GO

-- Check categories
SELECT c.id, c.name, c.color, u.username as owner
FROM categories c
JOIN users u ON c.user_id = u.id;
GO

-- Check tasks
SELECT 
    t.id,
    t.title,
    t.status,
    t.priority,
    t.due_date,
    u.username as owner,
    c.name as category
FROM tasks t
JOIN users u ON t.user_id = u.id
LEFT JOIN categories c ON t.category_id = c.id
ORDER BY t.due_date;
GO

-- ============================================
-- Useful Queries for Development
-- ============================================

-- Get tasks with category info
SELECT 
    t.id,
    t.title,
    t.description,
    t.status,
    t.priority,
    t.due_date,
    c.name as category_name,
    c.color as category_color,
    t.created_at
FROM tasks t
LEFT JOIN categories c ON t.category_id = c.id
WHERE t.user_id = 1
ORDER BY t.due_date;
GO

-- Get task statistics by status
SELECT 
    status,
    COUNT(*) as count
FROM tasks
WHERE user_id = 1
GROUP BY status;
GO

-- Get overdue tasks
SELECT 
    t.id,
    t.title,
    t.due_date,
    t.status
FROM tasks t
WHERE t.user_id = 1 
    AND t.due_date < CAST(GETDATE() AS DATE)
    AND t.status != 'DONE'
ORDER BY t.due_date;
GO


