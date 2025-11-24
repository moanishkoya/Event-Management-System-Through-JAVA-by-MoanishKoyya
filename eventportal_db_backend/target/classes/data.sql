INSERT INTO events (title, description, event_date, start_time, end_time, venue_name, status, event_type)
VALUES
  ('Tech Talk: AI in 2025',
   'A session on AI trends for students.',
   DATE_ADD(CURDATE(), INTERVAL 2 DAY),
   '10:00:00',
   '12:00:00',
   'Auditorium',
   'APPROVED',
   'MINOR'
  ),
  ('Cultural Fest',
   'Music, dance and fun events.',
   DATE_ADD(CURDATE(), INTERVAL 5 DAY),
   '17:00:00',
   '21:00:00',
   'Open Ground',
   'APPROVED',
   'MAJOR'
  ),
  ('Hackathon 2024',
   '24-hour coding challenge with multiple teams.',
   DATE_SUB(CURDATE(), INTERVAL 10 DAY),
   '09:00:00',
   '09:00:00',
   'Lab Block',
   'COMPLETED',
   'MAJOR'
  ),
  ('Sports Day',
   'Athletics and outdoor events.',
   DATE_SUB(CURDATE(), INTERVAL 20 DAY),
   '08:00:00',
   '16:00:00',
   'College Ground',
   'COMPLETED',
   'MINOR'
  );
