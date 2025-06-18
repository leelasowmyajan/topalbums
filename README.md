# Top Albums App 🎵

Welcome to the TopAlbums project! It's a simple fullstack application that allows you to keep track of your favorite albums. This app lets you **add**, **update**, and **delete** albums, as well as **upload album covers** and **update album covers**.

## Key Features:

* Add a new album with details like name, artist, genre, release year, and even a cover image.
* View album details on a dedicated page.
* Update album information and change album covers.
* Delete albums if you're no longer vibing with them.

### Here's what the main page looks like:

![Albums Main Page](UI%20Screenshots/Albums%20Main%20Page.png)

### This is how the "Add New Album" page looks:

![Albums Create Page](UI%20Screenshots/Albums%20Create%20Page.png)

### And here's the "Album Details" page:

![Albums Details Page](UI%20Screenshots/Albums%20Details%20Page.png)

## Why This Project?

I built this as a fun way to refresh my full-stack development skills. Here’s a breakdown of the technologies I used:

### Backend:

* **Java & Spring Boot**: The backend is powered by Spring Boot. It handles CRUD operations for albums, manages album cover uploads, and connects to the database.
* **PostgreSQL**: For storing album information. It’s run on Docker to keep things simple and smooth.
* **Testing with JUnit & Mockito**: I wrote unit and integration tests to make sure everything works as expected.

### Frontend:

* **React**: The frontend is built using React. I used hooks, components, and props to make the UI interactive.
* **Toastify**: This library is used to show action messages, like "Album successfully added!" or "Album deleted!"

---

## How to Get Started on Your System:

If you're interested in running this project on your local machine, here’s how you can set it up.

### 1. Clone the repo:

```bash
git clone https://github.com/leelasowmyajan/topalbums-fullstack.git
```

### Backend Setup:

#### Pre-requisites:

* Java (JDK 17 or higher)
* Docker

#### Steps:

1. Navigate to the `topalbums-backend` folder:

   ```bash
   cd topalbums-fullstack/topalbums-backend
   ```

2. Run the backend service using Docker:

   ```bash
   docker-compose up -d
   ```

3. Start the Spring Boot application:

   ```bash
   java -jar target/topalbums-backend-0.0.1-SNAPSHOT.jar
   ```

   Now, the backend should be running on `localhost:8080`.

---

### Frontend Setup:

#### Pre-requisites:

* Node.js 

#### Steps:

1. Navigate to the `topalbums-ui` folder:

   ```bash
   cd topalbums-fullstack/topalbums-ui
   ```

2. Install the necessary dependencies:

   ```bash
   npm install
   ```

3. Start the React app:

   ```bash
   npm start
   ```

   The frontend will now be running on `localhost:3000`.

## Final Thoughts:

This project is a simple but fun way to practice full-stack development. It covers essential concepts in both backend (Spring Boot) and frontend (React). And if you run into any issues or have suggestions, let me know!


