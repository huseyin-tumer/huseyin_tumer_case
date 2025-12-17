from locust import HttpUser, task, between


class SimpleWebUser(HttpUser):
    """
    A simple Locust user that:
    - Calls the home page `/`
    - Calls a health-check style endpoint `/health` (you can change this)
    """

    # Each simulated user waits between 1 and 5 seconds between requests
    wait_time = between(1, 5)

    @task(3)
    def load_home_page(self):
        """Hit the main page more frequently."""
        self.client.get("/")

    @task(1)
    def check_health(self):
        """Hit a health or status endpoint."""
        self.client.get("/arama?q=selam")

    def on_start(self):
        """
        Optional hook: runs once when a simulated user starts.
        Useful for login flows or setup.
        """
        # Example: self.client.post("/login", json={"user": "test", "password": "test"})
        pass


