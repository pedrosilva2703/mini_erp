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
