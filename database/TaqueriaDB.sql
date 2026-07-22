/*
---------------------------------------------------------------------------------------
                    SISTEMA DE VENTAS - TAQUERÍA EL BIGOTES
---------------------------------------------------------------------------------------
Nombre del proyecto:
    Sistema de gestión para Taquería El Bigotes

Descripción:
    Script de creación de la base de datos utilizada para administrar las 
    operaciones principales de la taquería.

Módulos incluidos:
    - Gestión de productos.
    - Control de insumos.
    - Administración de empleados y usuarios.
    - Control de inventario.
    - Registro de pedidos y detalles de venta.
    - Relación entre productos e ingredientes utilizados.

Motor de base de datos:
    Microsoft SQL Server

Fecha de creación:
    2026

Autor:
Kalecxa Guadalupe Sandoval Encines

*/


/*
1. CREACIÓN DE BASE DE DATOS
*/

CREATE DATABASE TaqueriaBD;
GO

USE TaqueriaBD;
GO



/*
2. TABLA PRODUCTO

Descripción:
    Almacena la información de los productos disponibles para la venta.

Campos principales:
    - idProducto: Identificador único del producto.
    - nombre: Nombre comercial del producto.
    - precio: Precio actual de venta.
    - descripcion: Información adicional del producto.
    - urlFoto: Ruta o dirección de la imagen asociada.
    - activo: Indica si el producto está disponible.
    - categoria: Clasificación del producto.

*/


CREATE TABLE Producto (
    idProducto INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    descripcion VARCHAR(255),
    urlFoto NVARCHAR(255),
    activo BIT NOT NULL DEFAULT 1
);


-- Se agrega la categoría para clasificar productos
ALTER TABLE Producto
ADD categoria VARCHAR(20) NOT NULL DEFAULT 'Comida';


-- Actualización preventiva de registros sin categoría
UPDATE Producto 
SET categoria = 'Comida' 
WHERE categoria IS NULL;



/*
3. TABLA INSUMO

Descripción:
    Contiene los ingredientes y materiales utilizados para la preparación
    de los productos.

Ejemplo:
    Carne, tortillas, queso, salsas, etc.

*/


CREATE TABLE Insumo (
    idInsumo INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    unidadMedida VARCHAR(50) NOT NULL
);



/*
4. TABLA EMPLEADO

Descripción:
    Guarda la información del personal que participa en las operaciones
    del sistema.

Campos:
    - nombre: Nombre del empleado.
    - puesto: Cargo dentro del negocio.
    - activo: Estado del empleado.

*/


CREATE TABLE Empleado (
    idEmpleado INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    puesto VARCHAR(50) NOT NULL,
    activo BIT NOT NULL DEFAULT 1
);



/*
5. TABLA USUARIO

Descripción:
    Administra las cuentas de acceso al sistema.

Relación:
    Cada usuario pertenece a un empleado registrado.

*/


CREATE TABLE Usuario (
    id_usuario INT IDENTITY(1,1) PRIMARY KEY,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(30) NOT NULL,
    id_empleado INT NOT NULL,

    CONSTRAINT FK_Usuario_Empleado
        FOREIGN KEY (id_empleado)
        REFERENCES Empleado(idEmpleado)
);



/*
6. TABLA INVENTARIO

Descripción:
    Permite controlar la cantidad disponible de cada insumo.

Relación:
    Un registro de inventario pertenece a un insumo.

*/


CREATE TABLE Inventario (
    idInventario INT IDENTITY(1,1) PRIMARY KEY,
    idInsumo INT NOT NULL,
    cantidadDisponible DECIMAL(10,2) NOT NULL,
    fechaActualizacion DATETIME NOT NULL DEFAULT GETDATE(),

    CONSTRAINT FK_Inventario_Insumo 
        FOREIGN KEY (idInsumo)
        REFERENCES Insumo(idInsumo)
);



/*
7. TABLA PEDIDO

Descripción:
    Registra las ventas realizadas en la taquería.

Información almacenada:
    - Fecha de venta.
    - Total del pedido.
    - Empleado encargado.
*/


CREATE TABLE Pedido (
    idPedido INT IDENTITY(1,1) PRIMARY KEY,
    fecha DATETIME NOT NULL DEFAULT GETDATE(),
    total DECIMAL(10,2) NOT NULL,
    idEmpleado INT NOT NULL,

    CONSTRAINT FK_Pedido_Empleado
        FOREIGN KEY (idEmpleado)
        REFERENCES Empleado(idEmpleado)
);



/*
8. TABLA DETALLE PEDIDO

Descripción:
    Guarda los productos incluidos dentro de cada pedido.

Relaciones:
    - Un pedido puede tener varios productos.
    - Un producto puede aparecer en diferentes pedidos.

*/


CREATE TABLE DetallePedido (
    idDetalle INT IDENTITY(1,1) PRIMARY KEY,
    idPedido INT NOT NULL,
    idProducto INT NOT NULL,
    cantidad INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,

    CONSTRAINT FK_DetallePedido_Pedido
        FOREIGN KEY (idPedido)
        REFERENCES Pedido(idPedido),

    CONSTRAINT FK_DetallePedido_Producto
        FOREIGN KEY (idProducto)
        REFERENCES Producto(idProducto)
);



/*
9. TABLA PRODUCTOINSUMO

Descripción:
    Tabla intermedia que representa la relación muchos a muchos entre productos
    e insumos.

Ejemplo:
    Un taco utiliza carne, tortilla, cebolla y salsa.
    Un insumo puede utilizarse en diferentes productos.

*/


CREATE TABLE productoInsumo (
    idProducto INT NOT NULL,
    idInsumo INT NOT NULL,

    CONSTRAINT PK_productoInsumo
        PRIMARY KEY (idProducto, idInsumo),

    CONSTRAINT FK_productoInsumo_Producto
        FOREIGN KEY (idProducto)
        REFERENCES Producto(idProducto),

    CONSTRAINT FK_productoInsumo_Insumo
        FOREIGN KEY (idInsumo)
        REFERENCES Insumo(idInsumo)
);



/*
10. CARGA INICIAL DE INSUMOS

Descripción:
    Inserción de ingredientes básicos utilizados por la taquería.
*/


INSERT INTO Insumo (nombre, unidadMedida) VALUES
('Carne de Res', 'Kilogramos'),
('Carne de Puerco', 'Kilogramos'),
('Tortilla de Maíz', 'Piezas'),
('Tortilla de Harina', 'Piezas'),
('Cebolla', 'Kilogramos'),
('Cilantro', 'Kilogramos'),
('Salsa Roja', 'Litros'),
('Salsa Verde', 'Litros'),
('Limón', 'Kilogramos'),
('Queso', 'Kilogramos');



/*
11. CARGA INICIAL DEL INVENTARIO

Descripción:
    Registro inicial de existencias disponibles para cada insumo.

*/


INSERT INTO Inventario (idInsumo, cantidadDisponible) VALUES

(1,60.00),    -- Carne de Res
(2,45.00),    -- Carne de Puerco
(3,800.00),   -- Tortilla de Maíz
(4,500.00),   -- Tortilla de Harina
(5,25.00),    -- Cebolla
(6,20.00),    -- Cilantro
(7,15.00),    -- Salsa Roja
(8,18.00),    -- Salsa Verde
(9,12.00),    -- Limón
(10,35.00);   -- Queso



/*
12. CREACIÓN DE USUARIO ADMINISTRADOR

Descripción:
    Se crea un usuario inicial con permisos administrativos para acceder
    al sistema.

Credenciales iniciales:
    Usuario: Admin
    Password: Admin123
*/


INSERT INTO Empleado(nombre, puesto)
VALUES
('Admin','Administrador');


INSERT INTO Usuario(usuario, password, rol, id_empleado)
VALUES
('Admin','Admin123','Administrador',1);



/*
13. CONSULTA DE VALIDACIÓN
Descripción:
    Consulta utilizada para verificar la creación correcta del usuario.
*/

SELECT * 
FROM Usuario;