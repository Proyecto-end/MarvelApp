# Marvel Studios - Aplicación Android

Una aplicación móvil completa para Disney Marvel Studios desarrollada en Java para Android, que permite a los usuarios explorar el universo Marvel, gestionar superhéroes favoritos y solicitar comics.

## 🚀 Características Principales

### 🔐 Sistema de Autenticación
- **Login**: Validación de credenciales con usuario y contraseña
- **Registro**: Formulario completo con validaciones exhaustivas
- **Gestión de Sesiones**: Persistencia automática de sesión de usuario
- **Credenciales de Prueba**: admin/123456

### 🏠 Pantalla Principal
- **Navegación por Fragments**: Home, Solicitar Comic, Configuración
- **Toolbar Personalizada**: Con búsqueda, notificaciones y logout
- **Bottom Navigation**: Navegación fluida entre secciones

### 🦸‍♂️ Fragment Home - Superhéroes
- **RecyclerView**: Lista de superhéroes Marvel con adaptador personalizado
- **Búsqueda en Tiempo Real**: Filtrado por nombre, descripción y categoría
- **Filtros por Universo**: Todos, Avengers, X-Men, 4 Fantásticos, Spider-Verse, Guardianes
- **Sistema de Favoritos**: Agregar/quitar superhéroes favoritos
- **Swipe to Refresh**: Actualización de contenido
- **Estados de UI**: Carga, vacío, error

### 📚 Fragment Solicitar Comic
- **Spinner de Comics**: Lista de comics Marvel disponibles
- **Vista Previa**: Información detallada del comic seleccionado
- **Validaciones Completas**: Cantidad, stock, motivo, prioridad
- **Sistema de Prioridades**: Baja, Media, Alta, Urgente
- **Persistencia**: Guardado de solicitudes en SharedPreferences

### ⚙️ Fragment Configuración
- **Perfil de Usuario**: Información personal editable
- **Estadísticas**: Comics favoritos, héroes seguidos, solicitudes
- **Configuraciones**: Notificaciones, tema oscuro, sonidos
- **Modo Edición**: Validaciones para actualización de perfil

### 🔍 Detalle de Superhéroe
- **Información Completa**: Descripción, universo, popularidad
- **Poderes Detallados**: Lista de 6 poderes por superhéroe
- **Comics Relacionados**: 5 comics por superhéroe
- **Funcionalidad Social**: Compartir superhéroe
- **Gestión de Favoritos**: Con animaciones

## 🎨 Diseño y UI/UX

### 🎭 Tema Marvel
- **Colores**: Rojo Marvel (#ED1D24), Azul (#1E90FF), Púrpura (#663399)
- **Fondo**: Negro con elementos en gris oscuro
- **Tipografía**: Material Design con fuentes personalizadas
- **Iconografía**: 20+ iconos vectoriales personalizados

### ✨ Animaciones
- **Lottie**: Animaciones de carga profesionales
- **Transiciones**: Fade in/out, slide animations
- **Micro-interacciones**: Ripple effects, button states
- **Estados Visuales**: Loading, empty, error states

## 🛠️ Tecnologías Utilizadas

### 📱 Android Nativo
- **Lenguaje**: Java
- **SDK**: Android API 24+ (Android 7.0)
- **Architecture**: Activity + Fragments
- **UI**: Material Design 3

### 📦 Dependencias Principales
- **RecyclerView**: Listas eficientes
- **CardView**: Tarjetas de contenido
- **Navigation Component**: Navegación entre fragments
- **Lottie**: Animaciones vectoriales
- **Picasso**: Carga de imágenes
- **Material Design**: Componentes UI modernos

### 💾 Persistencia de Datos
- **SharedPreferences**: Datos de usuario, sesiones, configuraciones
- **Estructura Organizada**: JSON para datos complejos
- **Gestión de Estado**: Sincronización automática

## 📊 Datos Incluidos

### 🦸‍♂️ Superhéroes (8 personajes)
- **Spider-Man**: Spider-Verse, poderes arácnidos
- **Iron Man**: Avengers, tecnología avanzada
- **Wolverine**: X-Men, factor de curación
- **Captain America**: Avengers, súper soldado
- **Doctor Strange**: Mystic Arts, magia
- **Storm**: X-Men, control climático
- **Mr. Fantastic**: 4 Fantásticos, elasticidad
- **Star-Lord**: Guardianes, liderazgo

### 📚 Comics (8 títulos)
- **Amazing Spider-Man**: $15.99, Stock: 25
- **Avengers Endgame**: $18.99, Stock: 15
- **X-Men Days of Future Past**: $16.99, Stock: 20
- **Iron Man Armor Wars**: $17.99, Stock: 12
- **Doctor Strange Multiverse**: $19.99, Stock: 8
- **Fantastic Four**: $14.99, Stock: 30
- **Guardians of the Galaxy**: $16.99, Stock: 18
- **Captain America**: $15.99, Stock: 22

## 🏗️ Estructura del Proyecto

```
app/src/main/
├── java/com/example/quiz2/
│   ├── LoginActivity.java              # Pantalla de login
│   ├── RegistroActivity.java           # Pantalla de registro
│   ├── MainMarvelActivity.java         # Actividad principal
│   ├── DetalleSuperheroActivity.java   # Detalle de superhéroe
│   ├── HomeFragment.java               # Fragment de superhéroes
│   ├── SolicitarFragment.java          # Fragment de solicitudes
│   ├── ConfiguracionFragment.java      # Fragment de configuración
│   ├── SuperheroeAdapter.java          # Adaptador RecyclerView
│   └── models/
│       ├── Usuario.java                # Modelo de usuario
│       ├── Superheroe.java            # Modelo de superhéroe
│       └── Comic.java                 # Modelo de comic
├── res/
│   ├── layout/                        # Layouts XML
│   ├── drawable/                      # Iconos e imágenes
│   ├── values/                        # Colores, strings, temas
│   ├── menu/                          # Menús
│   └── anim/                          # Animaciones
└── AndroidManifest.xml                # Configuración de la app
```

## 🚀 Instalación y Configuración

### 📋 Requisitos
- Android Studio Arctic Fox o superior
- SDK Android 24+ (Android 7.0)
- Gradle 7.0+
- Java 8+

### 🔧 Pasos de Instalación
1. **Clonar el repositorio**
   ```bash
   git clone [URL_DEL_REPOSITORIO]
   cd quiz2-master
   ```

2. **Abrir en Android Studio**
   - File → Open → Seleccionar carpeta del proyecto
   - Esperar sincronización de Gradle

3. **Configurar dependencias**
   ```bash
   ./gradlew clean build
   ```

4. **Ejecutar la aplicación**
   - Conectar dispositivo Android o usar emulador
   - Run → Run 'app'

## 🧪 Testing y Validación

### 👤 Usuarios de Prueba
- **Admin**: admin / 123456
- **Registro**: Crear nuevos usuarios con validaciones

### 🔍 Casos de Prueba
1. **Login/Registro**: Validaciones de formularios
2. **Navegación**: Transiciones entre fragments
3. **Búsqueda**: Filtrado en tiempo real
4. **Favoritos**: Agregar/quitar superhéroes
5. **Solicitudes**: Validaciones y persistencia
6. **Configuración**: Edición de perfil

## 📱 Capturas de Pantalla

### 🔐 Autenticación
- Pantalla de login con animación Lottie
- Formulario de registro con validaciones
- Gestión de errores y estados

### 🏠 Pantalla Principal
- Home con lista de superhéroes
- Filtros y búsqueda
- Bottom navigation

### 📚 Funcionalidades
- Solicitud de comics
- Configuración de perfil
- Detalle de superhéroe

## 🤝 Contribución

### 📝 Guías de Desarrollo
1. **Código**: Seguir convenciones Java
2. **UI**: Mantener consistencia con Material Design
3. **Colores**: Usar paleta Marvel definida
4. **Animaciones**: Implementar transiciones suaves

### 🐛 Reporte de Bugs
- Usar Issues de GitHub
- Incluir pasos para reproducir
- Adjuntar logs y capturas

## 📄 Licencia

Este proyecto es desarrollado para fines educativos y de demostración.

## 👨‍💻 Desarrollador

Desarrollado con ❤️ para Disney Marvel Studios

---

**Marvel Studios App** - Explora el universo de superhéroes en tu dispositivo Android 