SELECT  client.id   AS id,
        client.name AS name,
        COUNT(client_order.id) AS orders_qty
FROM client
JOIN client_order ON client.id = client_order.FK_client
GROUP BY client.id

INSERT INTO client_order (type, status) VALUES ('Blue', 'pending', 1);