# Documentación del Proceso Técnico

Este README describe, de forma clara y directa, el **paso a paso del proceso técnico** que se siguió durante el desarrollo del proyecto.

En particular, se documenta:
- El uso de **Circe** para procesar JSON
- Cómo se separaron (rompieron) JSON complejos para adaptarlos a un modelo relacional
- Cómo se limpiaron y transformaron datos provenientes de **Excel / CSV**

El proyecto fue desarrollado en **Scala 3**, utilizando **Cats Effect, FS2 y Circe**, aplicando lo aprendido en clase sobre limpieza de datos y normalización.

---

## 1. Uso de Circe para el procesamiento de JSON

### 1.1 Definición de modelos
Se definieron `case class` que representan exactamente la estructura de los JSON originales.

Ejemplo (archivo `ConsultaCirce.scala`):
- `Genre`
- `CrewRaw`
- `Movie`

Esto permite que Circe realice **decodificación automática** usando:
```scala
import io.circe.generic.auto._
```

---

### 1.2 Decodificación de JSON
Se utilizó `decode[T]` de Circe para convertir strings JSON en estructuras Scala:

- `decode[List[Genre]](json)`
- `decode[List[CrewRaw]](json)`

En caso de error, se decidió:
- Retornar listas vacías (`getOrElse(List.empty)`)
- O lanzar la excepción (en ejemplos controlados)

Esto permitió **evitar fallos del pipeline** ante datos corruptos.

---

## 2. Ruptura (normalización) de JSON

Los JSON originales contenían **estructuras anidadas**, lo cual no es compatible directamente con un modelo relacional.

### 2.1 JSON → Entidades independientes

Ejemplo: `genres`

JSON original:
```json
[{"id":18,"name":"Drama"},{"id":80,"name":"Crime"}]
```

Se rompió en:
- Tabla `GENRE (id, name)`
- Tabla intermedia `MOVIE_GENRE (movie_id, genre_id)`

Implementación:
- `extractUniqueGenres`
- `extractMovieGenres`

---

### 2.2 JSON → Relaciones muchos-a-muchos

Ejemplo: `crew`

Un solo JSON generó **dos tablas**:
- `CREW` (entidad)
- `MOVIE_CREW` (relación)

Se separó:
- Información única de la persona (`id`, `name`, `gender`)
- Información contextual de la película (`department`, `job`, `credit_id`)

Esto garantiza:
- Normalización
- No duplicación de datos

---

## 3. Limpieza y validación de datos

### 3.1 Manejo de valores nulos
Se estableció como **regla de negocio**:
> Ningún dato puede ser `null`

Soluciones aplicadas:
- `Option[T]` en campos opcionales
- Valores por defecto (`0`, `"Sin dato"`)

Ejemplo:
```scala
gender.getOrElse(0)
profile_path.getOrElse("Sin dato")
```

---

### 3.2 Limpieza de tipos primitivos

Datos provenientes de CSV / Excel fueron tratados como `String` y luego convertidos:

- `String → Int`
- `String → Double`

Funciones reutilizables:
```scala
def cleanInt(value: String): Int =
  value.toIntOption.getOrElse(0)
```

Esto evita errores por:
- Cadenas vacías
- Espacios en blanco
- Valores inválidos

---

## 4. Transformación de datos desde Excel / CSV

### 4.1 Lectura del CSV
Se utilizó **FS2 + fs2-data-csv** para procesar archivos grandes de forma funcional y segura:

- Lectura en streaming
- Separador `;`
- Acceso por índice de columna

Archivo: `ColumnasTextoFrecuencia.scala`

---

### 4.2 Validación por dominio

Se validaron campos textuales usando catálogos cerrados:

Ejemplo:
- Idiomas permitidos: `en`, `es`, `fr`, etc.
- Estados válidos: `released`, `rumored`, etc.

Esto permitió:
- Eliminar valores corruptos
- Detectar datos atípicos

---

### 4.3 Normalización de texto

Antes de insertar datos:
- `trim`
- `toLowerCase`

Esto evita duplicados como:
- `EN`, `en `, ` En`

---

