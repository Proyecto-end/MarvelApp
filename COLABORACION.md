# 🤝 Guía de Colaboración - Marvel App

## 📋 Configuración Inicial

### 1. Clonar el repositorio
```bash
git clone https://github.com/Proyecto-end/MarvelApp.git
cd MarvelApp
```

### 2. Configurar tu usuario Git
```bash
git config user.name "Tu Nombre"
git config user.email "tu-email@ejemplo.com"
```

## 🌳 Estructura de Ramas

- **`main`** → Código en producción (NO tocar directamente)
- **`develop`** → Rama principal de desarrollo
- **`feature/nombre`** → Nuevas características
- **`hotfix/nombre`** → Correcciones urgentes

## 🔄 Flujo de Trabajo

### Para desarrollar una nueva característica:

1. **Cambiar a develop y actualizar:**
```bash
git checkout develop
git pull origin develop
```

2. **Crear tu rama feature:**
```bash
git checkout -b feature/mi-nueva-caracteristica
```

3. **Desarrollar y hacer commits:**
```bash
git add .
git commit -m "feat: descripción de la característica"
```

4. **Subir tu rama:**
```bash
git push -u origin feature/mi-nueva-caracteristica
```

5. **Crear Pull Request en GitHub:**
   - Ve a GitHub
   - Clic en "Compare & pull request"
   - Base: `develop` ← Compare: `feature/mi-nueva-caracteristica`
   - Descripción detallada de cambios
   - Solicitar review

## 📝 Convenciones de Commits

- `feat:` Nueva característica
- `fix:` Corrección de bug
- `docs:` Documentación
- `style:` Cambios de formato
- `refactor:` Refactorización
- `test:` Agregar tests

**Ejemplos:**
```
feat: agregar superhéroe Deadpool
fix: corregir crash en lista de favoritos
docs: actualizar README con nuevas funciones
```

## 🎯 Ramas Disponibles

### 🎨 `feature/mejorar-ui`
**Responsable:** [Asignar colaborador]
- Mejorar diseño de cards
- Actualizar colores y tipografías
- Agregar animaciones

### 🦸‍♂️ `feature/avances` 
**Responsable:** [Asignar colaborador]
- Agregar nuevos superhéroes
- Implementar nuevos poderes
- Crear biografías detalladas

### 📱 Próximas ramas sugeridas:
- `feature/notificaciones-push`
- `feature/modo-oscuro`
- `feature/compartir-social`
- `feature/comics-api`

## ⚠️ Reglas Importantes

1. **NUNCA** hacer push directo a `main`
2. **SIEMPRE** crear Pull Request
3. **ESPERAR** review antes de merge
4. **ACTUALIZAR** develop antes de crear feature
5. **PROBAR** tu código antes de push

## 🔧 Comandos Útiles

```bash
# Ver todas las ramas
git branch -a

# Cambiar de rama
git checkout nombre-rama

# Actualizar rama actual
git pull origin nombre-rama

# Ver status
git status

# Ver historial
git log --oneline

# Descartar cambios locales
git checkout -- archivo.java
```

## 👥 Solicitar Ayuda

- **Issues en GitHub:** Para bugs y preguntas
- **Discord/Slack:** Para comunicación rápida
- **Pull Request:** Para revisión de código

## 📱 Testing

Antes de hacer Pull Request:
1. Compilar sin errores: `./gradlew build`
2. Probar en dispositivo/emulador
3. Verificar que no hay crashes
4. Revisar funcionalidad completa

---

**¡Bienvenido al equipo Marvel! 🦸‍♂️** 