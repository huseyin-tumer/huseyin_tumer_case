## Locust Load Test Setup

This project contains a simple, well-structured example of how to run HTTP load tests using **Locust**.

### 1. Install dependencies

```bash
pip install -r requirements.txt
```

### 2. Run the load test

The main locust file is in `loadtests/locustfile.py`.  
From the project root (`locust` directory), run:

```bash
locust -f loadtests/locustfile.py --host=http://your-target-host
```

Then open the Locust web UI (by default at `http://localhost:8089`), enter:

- **Number of users** (e.g. 10)
- **Spawn rate** (e.g. 2)
- **Host** (if you didn't pass `--host` on the CLI)

and start the test.

### 3. Structure

- `main.py` – small helper/entry file with basic instructions.
- `requirements.txt` – Python dependencies (Locust).
- `loadtests/locustfile.py` – Locust user definition and simple test scenario.


