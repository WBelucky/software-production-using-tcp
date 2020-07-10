#!/bin/bash
cd frontend && npm i && npm run build && cd ../backend && mkdir -p static && cp -r ../frontend/dist/* static && gradle run
