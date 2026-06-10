DELETE FROM activity_evaluation WHERE evaluator_id IN (1, 2, 6) OR student_id IN (1, 2, 6);
DELETE FROM credit_record WHERE student_id IN (1, 2, 6);
DELETE FROM activity_registration WHERE student_id IN (1, 2, 6);
DELETE FROM activity WHERE organizer_id IN (1, 2, 6);
DELETE FROM system_notification WHERE user_id IN (1, 2, 6);
DELETE FROM contact_relation WHERE requester_id IN (1, 2, 6) OR addressee_id IN (1, 2, 6);
DELETE FROM sys_user WHERE id IN (1, 2, 6);