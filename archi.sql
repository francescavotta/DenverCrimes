SELECT e1.offense_type_id, e2.offense_type_id, COUNT(DISTINCT e1.neighborhood_id) AS peso
FROM events e1, events e2
WHERE e1.offense_category_id = ? AND
e1.offense_category_id = e2.offense_category_id AND
MONTH(e1.reported_date) = ? AND
MONTH(e1.reported_date) = MONTH(e2.reported_date) AND
e1.offense_type_id > e2.offense_type_id AND
e1.neighborhood_id = e2.neighborhood_id
GROUP BY e1.offense_type_id, e2.offense_type_id
