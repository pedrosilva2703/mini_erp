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

