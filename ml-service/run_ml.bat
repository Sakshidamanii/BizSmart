@echo off
echo ===================================================================
echo Starting BizSmart Demand Prediction Microservice (FastAPI)...
echo ===================================================================
cd /d "%~dp0"
python -m uvicorn app:app --port 8000 --reload
pause
