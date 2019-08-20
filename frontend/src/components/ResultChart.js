import React, { Component } from 'react'
import axios from 'axios'
import Chart from 'react-google-charts';
import { Container } from 'react-bootstrap';

class ResultChart extends Component {
    constructor(props) {
        super(props);
        this.state = {
            alertData: {},
            resultData: []
        };
    }

    SuccessChartHeader = [
        { type: "datetime", id: "timestamp", label: "Request Time" },
        { type: "number", id: "success", label: "Success" }
    ];

    ElapsedChartHeader = [
        { type: "datetime", id: "timestamp", label: "Request Time" },
        { type: "number", id: "elapsed", label: "RTT (ms)" }
    ];

    componentDidMount() {
        axios.get(
            "http://localhost:8080/alert/" + this.props.alertId
        ).then((resp) => {
            this.setState({alertData: resp.data});
        }).catch((err) => {
            console.error("Error on axios alert get: " + err);
        })
        this.refreshTimer = setInterval(this.retrieveChartData, 1000);
    }

    componentWillUnmount() {
        clearInterval(this.refreshTimer);
    }

    componentDidUpdate(prevProps, prevState) {
        if (this.props !== prevProps) {
            this.retrieveChartData();
        }
    }

    retrieveChartData = () => {
        if (this.props.alertId !== -1) {
            axios.get(
                "http://localhost:8080/results/" + this.props.alertId
            ).then((resp) => {
                this.setState({ resultData: resp.data });
            }).catch((err) => {
                console.error("Error on axios result get: " + err);
            });
        }
    }

    render() {
        return (
            <Container className="ResultChartBox">
                <h3>Results for scheduled alert named: <code>{this.state.alertData.alertName}</code></h3>
                <Chart
                    chartType="ScatterChart"
                    loader={<div>Loading Chart</div>}
                    data={
                        [this.SuccessChartHeader].concat(this.state.resultData.map((resultElem) => { 
                            return [new Date(resultElem.timestamp), resultElem.success ? 1 : 0]
                        }))
                    }
                    options={{
                        chart: {
                            title: "Request Results",
                        },
                        vAxis: {
                            minValue: 0,
                            maxValue: 1
                        }
                    }}
                />
                <br />
                <Chart
                    chartType="ScatterChart"
                    loader={<div>Loading Chart</div>}
                    data={
                        [this.ElapsedChartHeader].concat(this.state.resultData.map((resultElem) => { 
                            return [new Date(resultElem.timestamp), resultElem.elapsed]
                        }))
                    }
                    options={{
                        chart: {
                            title: "Request Results",
                        },
                        vAxis: {
                            minValue: 0,
                            maxValue: 1000
                        }
                    }}
                />
            </Container>
        );
    }
}

export default ResultChart;