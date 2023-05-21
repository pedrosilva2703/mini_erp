SELECT  client.id   AS id,
        client.name AS name,
        COUNT(client_order.id) AS orders_qty
FROM client
JOIN client_order ON client.id = client_order.FK_client
GROUP BY client.id

INSERT INTO client_order (type, status) VALUES ('Blue', 'pending', 1);

INSERT INTO supplier (name) VALUES ('Coiso');
INSERT INTO supplier_material (min_quantity, unit_price, delivery_time, FK_supplier, FK_material)
    SELECT 20, 5, 3, supplier.id, material.id
    FROM supplier, material
    WHERE supplier.name = 'Coiso' AND material.type = 'GreenRawMaterial';


SELECT  supplier.id,
        supplier.name,
        material.type,
        supplier_material.unit_price,
        supplier_material.min_quantity,
        supplier_material.delivery_time
FROM supplier_material
JOIN supplier on supplier.id = supplier_material.FK_supplier
JOIN material on material.id = supplier_material.FK_material
ORDER BY supplier.id ASC

SELECT  supplier.id,
        supplier.name,
        material.type,
        supplier_material.unit_price,
        supplier_material.min_quantity,
        supplier_material.delivery_time
FROM supplier_material
JOIN supplier on supplier.id = supplier_material.FK_supplier
JOIN material on material.id = supplier_material.FK_material
WHERE material.type='GreenRawMaterial' AND supplier_material.min_quantity >= 10

ORDER BY supplier_material.unitprice ASC


SELECT  COUNT(piece.id)
FROM    piece
JOIN    production_order    ON piece.FK_production_order = production_order.id
WHERE   production_order.week = 1

INSERT INTO supplier_order (type, quantity, unit_price, week_est_delivery, delay, FK_supplier)
VALUES  (?, ?, ?, ?, ?, ?)
RETURNING id


INSERT INTO client_order (type, price, week_est_delivery, delay, status, FK_client)
    SELECT ?, ?, ?, ?, ?, client.id
    FROM client
    WHERE client.name = ?


INSERT INTO piece ( type, status, final_type, FK_supplier_order, FK_inbound_order, FK_production_order)
VALUES (?, ?, ?, ?, ?, ?)


SELECT  supplier.name,
        supplier_order.type,
        supplier_order.quantity,
        supplier_order.unit_price,
        supplier_order.week_est_delivery,
        supplier_order.delay,
        supplier_order.status
FROM supplier_order
JOIN supplier ON supplier.id = supplier_order.FK_supplier



UPDATE supplier_material
SET min_quantity = ?, unit_price = ?, delivery_time = ?
WHERE   FK_supplier IN (SELECT supplier.id FROM supplier WHERE supplier.name = ?)
        AND
        FK_material IN (SELECT material.id FROM material WHERE material.type = ?)

SELECT COUNT(*)
FROM supplier_material
WHERE   FK_supplier IN (SELECT supplier.id FROM supplier WHERE supplier.name = ?)
        AND
        FK_material IN (SELECT material.id FROM material WHERE material.type = ?)


SELECT  client_order.id,
        client.name,
        client_order.type,
        client_order.quantity,
        client_order.price,
        client_order.week_est_delivery,
        client_order.delay,
        client_order.status
FROM client_order
JOIN client on client.id = client_order.FK_client
WHERE client_order.status = ?

UPDATE client_order
SET status = ?
WHERE client_order.id = ?


UPDATE inbound_order
SET status = ?
WHERE id IN
(SELECT piece.fk_inbound_order
FROM piece
WHERE piece.FK_client_order = 1)


/* perguntar esta */
SELECT COUNT(*)
FROM supplier_order
WHERE   FK_supplier IN (SELECT supplier.id FROM supplier WHERE supplier.name = ?)
        AND
        (status = 'waiting_confirmation' OR status = 'ordered')


SELECT  id,
        type,
        status,
        final_type,
        week_arrived,
        week_produced,
        duration_production,
        safety_stock,
        wh_pos
FROM piece
WHERE FK_inbound_order = ?


SELECT  id,
        week,
        status,
        FK_supplier_order
FROM inbound_order


DELETE FROM inbound_order
WHERE status = 'canceled';
DELETE FROM production_order
WHERE status = 'canceled';
DELETE FROM expedition_order
WHERE status = 'canceled';



SELECT COUNT(piece.id), SUM(CASE WHEN piece.week_arrived IS NOT NULL THEN 1 ELSE 0 END)
FROM piece
JOIN inbound_order ON piece.fk_inbound_order = inbound_order.id
WHERE inbound_order.id IN
    (SELECT piece.fk_inbound_order
     FROM piece
     WHERE piece.id = ?)