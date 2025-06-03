const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');

const app = express();
const port = 3000;

// Middleware
app.use(cors());
app.use(bodyParser.json());

// Almacenamiento temporal de usuarios (en memoria)
const users = [];

// Ruta de registro
app.post('/api/auth/register', (req, res) => {
    const { name, email, password, birthDate } = req.body;

    // Validaciones básicas
    if (!name || !email || !password || !birthDate) {
        return res.status(400).json({
            success: false,
            message: 'Todos los campos son requeridos'
        });
    }

    // Verificar si el email ya existe
    if (users.some(user => user.email === email)) {
        return res.status(400).json({
            success: false,
            message: 'El email ya está registrado'
        });
    }

    // Crear nuevo usuario
    const newUser = {
        id: users.length + 1,
        name,
        email,
        password, // En producción, esto debería estar hasheado
        birthDate
    };

    users.push(newUser);

    // Respuesta exitosa
    res.json({
        success: true,
        message: 'Usuario registrado exitosamente',
        user: {
            id: newUser.id,
            name: newUser.name,
            email: newUser.email,
            birthDate: newUser.birthDate
        }
    });
});

// Ruta de login
app.post('/api/auth/login', (req, res) => {
    const { email, password } = req.body;

    // Validaciones básicas
    if (!email || !password) {
        return res.status(400).json({
            success: false,
            message: 'Email y contraseña son requeridos'
        });
    }

    // Buscar usuario
    const user = users.find(u => u.email === email && u.password === password);

    if (!user) {
        return res.status(401).json({
            success: false,
            message: 'Credenciales inválidas'
        });
    }

    // Respuesta exitosa
    res.json({
        success: true,
        message: 'Login exitoso',
        token: 'dummy-token-' + Date.now(), // En producción, esto debería ser un JWT real
        user: {
            id: user.id,
            name: user.name,
            email: user.email,
            birthDate: user.birthDate
        }
    });
});

// Iniciar servidor
app.listen(port, '0.0.0.0', () => {
    console.log(`Servidor corriendo en http://localhost:${port}`);
    console.log(`También accesible en http://192.168.1.59:${port}`);
}); 