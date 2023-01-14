kubectl apply -f jaeger-server-configmap.yaml
kubectl apply -f prometheus-configmap.yaml
kubectl apply -f otel-configmap.yaml
kubectl apply -f jaeger-deployment.yaml
kubectl apply -f jaeger-service.yaml
kubectl apply -f otel-collector-deployment.yaml
kubectl apply -f otel-collector-service.yaml
kubectl apply -f prometheus-deployment.yaml
kubectl apply -f prometheus-service.yaml

kubectl apply -f activity-deployment.yaml
kubectl apply -f activity-service.yaml
kubectl apply -f hobby-deployment.yaml
kubectl apply -f hobby-service.yaml
