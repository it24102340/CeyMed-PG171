-- Fix Missing Feedback Table for Friends/Other Computers
USE CeyMedDb5;

-- Create the feedback table if it doesn't exist
CREATE TABLE IF NOT EXISTS feedback (
    feedback_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    policy_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comments TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Verify the table was created
DESCRIBE feedback;

-- Show success message
SELECT 'Feedback table created successfully!' as message;
SELECT 'Your friend can now run the project without errors.' as status;

