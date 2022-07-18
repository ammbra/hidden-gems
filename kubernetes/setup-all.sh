kubectl apply -f jaeger-server-configmap.yaml
kubectl apply -f prometheus-configmap.yaml
kubectl apply -f otel-configmap.yaml
kubectl apply -f jaeger-deployment.yaml
kubectl apply -f jaeger-service.yaml
kubectl apply -f otel-collector-deployment.yaml
kubectl apply -f otel-collector-service.yaml
oc new-app --docker-image=quay.io/prometheus/prometheus:latest
kubectl replace -f prometheus-deployment.yaml
oc expose svc prometheus
oc expose svc jaeger --port=16686 --generator="route/v1"

kubectl apply -f activity-deployment.yaml
kubectl apply -f activity-service.yaml
kubectl apply -f hobby-deployment.yaml
kubectl apply -f hobby-service.yaml


#curl -O "https://raw.githubusercontent.com/redhat-scholars/kube-native-java-apps/master/apps/kubefiles/{jaeger-server-configmap.yaml,prometheus-configmap.yaml,otel-configmap.yaml,jaeger-deployment.yaml,jaeger-service.yaml,otel-collector-deployment.yaml,prometheus-deployment.yaml}"